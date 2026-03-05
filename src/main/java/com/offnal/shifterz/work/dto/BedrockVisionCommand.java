package com.offnal.shifterz.work.dto;

public record BedrockVisionCommand(
	byte[] imageBytes,
	String imageFormat,   // "png" or "jpg"
	String base64Image    // 요구사항: Base64 로직 결과도 함께 보관(필요시 로깅/저장)
) {}
