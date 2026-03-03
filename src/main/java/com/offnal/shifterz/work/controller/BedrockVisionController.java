package com.offnal.shifterz.work.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.offnal.shifterz.work.dto.BedrockVisionResponse;
import com.offnal.shifterz.work.converter.BedrockVisionConverter;
import com.offnal.shifterz.work.service.BedrockVisionService;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bedrock")
public class BedrockVisionController {

	private final BedrockVisionService bedrockVisionService;
	private final BedrockVisionConverter bedrockVisionConverter;

	@Operation(summary = "이미지 분석", description = "이미지를 Bedrock(Claude Sonnet)에 전달해 분석 결과(JSON 텍스트)를 반환합니다.")
	@PostMapping(value = "/vision", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public SuccessResponse<BedrockVisionResponse> vision(
		@RequestPart("image") MultipartFile image
	) {
		var cmd = bedrockVisionConverter.toCommand(image);
		String bedrockText = bedrockVisionService.analyzeImage(cmd);

		BedrockVisionResponse result = bedrockVisionConverter.toResponse(bedrockText);
		return SuccessResponse.success(SuccessCode.BEDROCK_VISION_SUCCESS, result);
	}
}
