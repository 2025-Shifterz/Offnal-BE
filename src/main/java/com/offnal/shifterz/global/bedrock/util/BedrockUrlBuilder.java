package com.offnal.shifterz.global.bedrock.util;

import java.nio.charset.StandardCharsets;

import org.springframework.web.util.UriUtils;

public final class BedrockUrlBuilder {

	private BedrockUrlBuilder() {}

	public static String converseUrl(String region, String modelId) {
		// ✅ modelId는 path segment로 "한 번만" 인코딩해야 함
		String encodedModelId = UriUtils.encodePathSegment(modelId, StandardCharsets.UTF_8);
		return "https://bedrock-runtime.%s.amazonaws.com/model/%s/converse"
			.formatted(region, encodedModelId);
	}
}

