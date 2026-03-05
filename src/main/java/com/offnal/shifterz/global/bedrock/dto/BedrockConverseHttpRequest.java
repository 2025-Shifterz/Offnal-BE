package com.offnal.shifterz.global.bedrock.dto;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ 전체적으로 null 필드 제거
public record BedrockConverseHttpRequest(
	List<Message> messages,

	// (옵션) 혹시 스펙이 inferenceConfig를 요구하면 그대로 두고,
	// 만약 inferenceConfiguration을 요구하는 엔드포인트면 아래 JsonProperty로 바꿔.
	@JsonProperty("inferenceConfig")
	InferenceConfig inferenceConfig
) {
	public record Message(String role, List<Content> content) {}

	/**
	 * content item: {"text": "..."} 또는 {"image": {...}}
	 * - text, image 둘 중 하나만 세팅
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL) // ✅ 핵심: {"text":".."} / {"image":{..}} 형태로만 나가게
	public record Content(
		String text,
		Image image
	) {
		public static Content text(String text) {
			return new Content(text, null);
		}

		public static Content image(String format, String base64Bytes) {
			return new Content(null, new Image(format, new ImageSource(base64Bytes)));
		}
	}

	public record Image(String format, ImageSource source) {}

	public record ImageSource(String bytes) {}

	public record InferenceConfig(
		int maxTokens,
		double temperature
	) {}
}