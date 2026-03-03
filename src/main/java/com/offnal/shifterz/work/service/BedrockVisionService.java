package com.offnal.shifterz.work.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.work.dto.BedrockVisionCommand;
import com.offnal.shifterz.global.bedrock.prompt.PromptLoader;
import com.offnal.shifterz.global.bedrock.BedrockConverseClient;
import com.offnal.shifterz.global.bedrock.dto.BedrockConverseHttpRequest;
import com.offnal.shifterz.global.bedrock.util.BedrockConverseParser;
import com.offnal.shifterz.global.bedrock.util.BedrockUrlBuilder;
import com.offnal.shifterz.global.config.bedrock.BedrockProperties;
import com.offnal.shifterz.global.exception.ErrorReason;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class BedrockVisionService {

	private static final String DEFAULT_PROMPT =
		PromptLoader.load("prompts/work-calendar-vision.prompt.txt");

	private final BedrockConverseClient bedrockConverseClient;
	private final BedrockProperties props;
	private final ObjectMapper objectMapper;

	@PostConstruct
	public void checkBedrockEnv() {
		log.info("BEDROCK props region={}, modelId(raw)={}", props.region(), props.modelId());
		log.info("BEDROCK modelId(encoded)={}",
			UriUtils.encodePathSegment(props.modelId(), StandardCharsets.UTF_8));
	}

	public String analyzeImage(BedrockVisionCommand cmd) {

		String url = BedrockUrlBuilder.converseUrl(props.region(), props.modelId());
		log.info("BEDROCK url={}", url);

		String format = normalizeFormat(cmd.imageFormat());
		String base64 = Base64.getEncoder().encodeToString(cmd.imageBytes());

		BedrockConverseHttpRequest req = new BedrockConverseHttpRequest(
			List.of(new BedrockConverseHttpRequest.Message(
				"user",
				List.of(
					BedrockConverseHttpRequest.Content.image(format, base64),
					BedrockConverseHttpRequest.Content.text(DEFAULT_PROMPT)
				)
			)),
			new BedrockConverseHttpRequest.InferenceConfig(4096, 0.0) // ✅ 토큰 넉넉히 + temp 0
		);

		String rawJson = bedrockConverseClient.postConverse(url, req, props.timeout().responseMs());

		// ✅ 1) null/blank 절대 금지
		if (rawJson == null || rawJson.isBlank()) {
			throw new CustomException(BedrockErrorCode.BEDROCK_UPSTREAM_ERROR);
		}

		// ✅ 2) 정제 시도: 실패하면 원본 반환(=절대 null 방지)
		try {
			JsonNode root = objectMapper.readTree(rawJson);

			// stopReason 위치 2군데 모두 체크
			String stopReason = root.path("stopReason").asText("");
			if (stopReason.isBlank()) {
				stopReason = root.path("output").path("stopReason").asText("");
			}
			if ("max_tokens".equals(stopReason)) {
				throw new CustomException(BedrockErrorCode.BEDROCK_UPSTREAM_ERROR);
			}

			// Bedrock Converse 응답에서 text 꺼내기
			String text = root.path("output")
				.path("message")
				.path("content")
				.path(0)
				.path("text")
				.asText(null);

			if (text == null || text.isBlank()) {
				// text가 없으면 정제 불가 -> 원본 반환
				return rawJson;
			}

			// ✅ 모델이 준 JSON 문자열 파싱 후 한 줄로(minify)
			JsonNode extracted = objectMapper.readTree(text);
			return objectMapper.writeValueAsString(extracted);

		} catch (CustomException ce) {
			throw ce;
		} catch (Exception e) {
			// ✅ 정제 실패해도 null 반환 금지 -> 원본 그대로 반환
			log.warn("Bedrock output normalize failed. return raw. err={}", e.getMessage());
			return rawJson;
		}
	}

	private String normalizeFormat(String imageFormat) {
		if ("jpg".equalsIgnoreCase(imageFormat) || "jpeg".equalsIgnoreCase(imageFormat))
			return "jpeg";
		return "png";
	}

	@Getter
	@AllArgsConstructor
	public enum BedrockErrorCode implements ErrorReason {

		// ENV/설정 관련
		BEDROCK_CONFIG_MISSING("BRK001", HttpStatus.INTERNAL_SERVER_ERROR, "Bedrock 설정(Region/ModelId)이 누락되었습니다."),

		// 요청/입력 관련
		IMAGE_REQUIRED("BRK002", HttpStatus.BAD_REQUEST, "이미지 파일은 필수입니다."),
		IMAGE_UNSUPPORTED_FORMAT("BRK003", HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 포맷입니다. (png/jpg만 허용)"),
		IMAGE_PREPROCESS_FAILED("BRK004", HttpStatus.BAD_REQUEST, "이미지 전처리에 실패했습니다."),
		PROMPT_REQUIRED("BRK005", HttpStatus.BAD_REQUEST, "프롬프트는 필수입니다."),

		// Bedrock 호출 관련
		BEDROCK_RATE_LIMIT("BRK006", HttpStatus.TOO_MANY_REQUESTS, "Bedrock 요청이 많아 제한되었습니다. 잠시 후 다시 시도해주세요."),
		BEDROCK_TIMEOUT("BRK007", HttpStatus.GATEWAY_TIMEOUT, "Bedrock 응답 시간이 초과되었습니다."),
		BEDROCK_ACCESS_DENIED("BRK008", HttpStatus.FORBIDDEN, "Bedrock 접근 권한이 없습니다. API Key/권한/리전/모델을 확인해주세요."),
		BEDROCK_MODEL_NOT_FOUND("BRK009", HttpStatus.BAD_REQUEST, "Bedrock 모델을 찾을 수 없습니다. modelId를 확인해주세요."),
		BEDROCK_INVALID_REQUEST("BRK010", HttpStatus.BAD_REQUEST, "Bedrock 요청 형식이 올바르지 않습니다."),
		BEDROCK_UPSTREAM_ERROR("BRK011", HttpStatus.BAD_GATEWAY, "Bedrock 처리 중 오류가 발생했습니다."),
		BEDROCK_UNKNOWN("BRK999", HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 오류가 발생했습니다.");

		private final String code;
		private final HttpStatus status;
		private final String message;
	}
}
