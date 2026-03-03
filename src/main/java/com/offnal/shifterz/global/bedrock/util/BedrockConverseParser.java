package com.offnal.shifterz.global.bedrock.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class BedrockConverseParser {

	private BedrockConverseParser() {}

	public static String extractText(ObjectMapper om, String rawJson) {
		try {
			JsonNode root = om.readTree(rawJson);
			JsonNode content = root.path("output").path("message").path("content");
			if (!content.isArray()) return rawJson;

			StringBuilder sb = new StringBuilder();
			for (JsonNode block : content) {
				JsonNode text = block.get("text");
				if (text != null && !text.isNull()) sb.append(text.asText());
			}
			return sb.toString();
		} catch (Exception e) {
			return rawJson;
		}
	}
}
