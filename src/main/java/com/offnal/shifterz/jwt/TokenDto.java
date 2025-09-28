package com.offnal.shifterz.jwt;

import io.swagger.v3.oas.annotations.media.Schema;

public class TokenDto {

    @Schema(description = "토큰 재발급 요청 DTO")
    public record TokenReissueRequest(
            @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
            String refreshToken
    ) {}

    @Schema(description = "토큰 재발급 응답 DTO")
    public record TokenResponse(
            @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
            String accessToken,
            @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
            String refreshToken
    ) {}
}

