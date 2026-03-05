package com.offnal.shifterz.global.bedrock;

import java.net.URI;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.offnal.shifterz.work.service.BedrockVisionService;
import com.offnal.shifterz.global.exception.CustomException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class BedrockConverseClient {

	private static final int MAX_ATTEMPTS = 4;
	private static final long BASE_BACKOFF_MS = 400;

	private final WebClient bedrockWebClient;

	@Value("${aws.bedrock.api-key:}")
	private String apiKey;

	public String postConverse(String url, Object body, int responseMs) {

		if (apiKey == null || apiKey.isBlank()) {
			throw new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_CONFIG_MISSING);
		}

		for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
			try {
				String result = bedrockWebClient.post()
					.uri(URI.create(url))
					.header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.bodyValue(body)
					.retrieve()
					.bodyToMono(String.class)
					.block(Duration.ofMillis(responseMs));
				log.info("Bedrock body is null? {}", result == null);
				log.info("Bedrock body length={}", result == null ? -1 : result.length());
				log.info("Bedrock body={}", result);
				if (result == null || result.isBlank()) {
					log.error("Bedrock returned empty body. url={}", url);
					throw new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_UPSTREAM_ERROR);
				}

				return result;

			} catch (WebClientResponseException e) {

				int status = e.getRawStatusCode();
				String resp = safeBody(e);

				// 콘솔 도배 방지
				log.error("Bedrock HTTP {} error body: {}", status, truncate(resp, 900));

				if (shouldRetry(status) && attempt < MAX_ATTEMPTS) {
					sleep(backoff(BASE_BACKOFF_MS, attempt));
					continue;
				}

				throw mapToDomainException(status);

			} catch (Exception e) {
				log.error("Bedrock Unknown: {}", e.getMessage(), e);
				throw new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_UNKNOWN);
			}
		}

		throw new IllegalStateException("Unreachable");
	}

	private boolean shouldRetry(int status) {
		return status == 429 || status == 408 || status == 504;
	}

	private CustomException mapToDomainException(int status) {
		if (status == 429) return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_RATE_LIMIT);
		if (status == 408 || status == 504) return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_TIMEOUT);
		if (status == 401 || status == 403) return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_ACCESS_DENIED);
		if (status == 404) return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_MODEL_NOT_FOUND);
		if (status >= 400 && status < 500) return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_INVALID_REQUEST);
		if (status >= 500) return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_UPSTREAM_ERROR);
		return new CustomException(BedrockVisionService.BedrockErrorCode.BEDROCK_UNKNOWN);
	}

	private long backoff(long baseMs, int attempt) {
		long exp = (long) (baseMs * Math.pow(2, attempt - 1));
		long jitter = (long) (Math.random() * 150);
		return Math.min(5000, exp + jitter);
	}

	private void sleep(long ms) {
		try { Thread.sleep(ms); }
		catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
	}

	private String safeBody(WebClientResponseException e) {
		try { return e.getResponseBodyAsString(); }
		catch (Exception ex) { return "(no-body)"; }
	}

	private String truncate(String s, int max) {
		if (s == null) return "null";
		if (s.length() <= max) return s;
		return s.substring(0, max) + "...(truncated:" + s.length() + ")";
	}
}
