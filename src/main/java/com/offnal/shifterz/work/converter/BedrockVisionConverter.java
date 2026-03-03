package com.offnal.shifterz.work.converter;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.offnal.shifterz.work.dto.BedrockVisionCommand;
import com.offnal.shifterz.work.dto.BedrockVisionResponse;
import com.offnal.shifterz.work.service.ImagePreprocessor;
import com.offnal.shifterz.work.service.BedrockVisionService;
import com.offnal.shifterz.global.exception.CustomException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BedrockVisionConverter {

	private final ObjectMapper objectMapper;

	public BedrockVisionCommand toCommand(MultipartFile image) {
		if (image == null || image.isEmpty()) {
			throw new CustomException(BedrockVisionService.BedrockErrorCode.IMAGE_REQUIRED);
		}

		try {
			byte[] original = image.getBytes();

			ImagePreprocessor.ProcessedImage processed =
				ImagePreprocessor.preprocess(original, "png", 1024);

			String fmt = processed.format().toLowerCase();
			if (!fmt.equals("png") && !fmt.equals("jpg") && !fmt.equals("jpeg")) {
				throw new CustomException(BedrockVisionService.BedrockErrorCode.IMAGE_UNSUPPORTED_FORMAT);
			}

			return new BedrockVisionCommand(
				processed.bytes(),
				fmt,
				processed.base64()
			);

		} catch (CustomException e) {
			throw e;
		} catch (Exception e) {
			throw new CustomException(BedrockVisionService.BedrockErrorCode.IMAGE_PREPROCESS_FAILED);
		}
	}

	public BedrockVisionResponse toResponse(String bedrockText) {
		try {
			// JSON 문자열 -> JSON 객체로 변환
			JsonNode json = objectMapper.readTree(bedrockText);
			return new BedrockVisionResponse(json);

		} catch (Exception e) {
			// Bedrock이 JSON이 아닌 텍스트를 주면 fallback
			ObjectNode fallback = objectMapper.createObjectNode();
			fallback.put("rawText", bedrockText);
			fallback.put("parseError", true);
			return new BedrockVisionResponse(fallback);
		}
	}
}
