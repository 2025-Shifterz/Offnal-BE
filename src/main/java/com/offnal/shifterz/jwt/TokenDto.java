package com.offnal.shifterz.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

public class TokenDto {

    @Schema(description = "토큰 재발급 요청 DTO")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenReissueRequest {
        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        @JsonProperty("refreshToken")
        private String refreshToken;
    }

    @Schema(description = "토큰 재발급 응답 DTO")
    @Getter
    @AllArgsConstructor
    public static class TokenResponse {
        @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String accessToken;

        @Schema(description = "Refresh Token", example = "eyJhbGciOiJIUzI1NiJ9...")
        private String refreshToken;
    }
}
