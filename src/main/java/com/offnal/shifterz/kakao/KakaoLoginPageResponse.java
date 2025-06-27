package com.offnal.shifterz.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class KakaoLoginPageResponse {

    @Schema(description = "카카오 로그인 리다이렉트 URL", example = "https://kauth.kakao.com/oauth/authorize?client_id=abc123&redirect_uri=http://localhost:8080/login/callback&response_type=code")
    private String location;

    public KakaoLoginPageResponse(String location) {
        this.location = location;
    }
}