package com.offnal.shifterz.oauth.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class KakaoLoginRequest {
    @Schema(description = "Kakao에서 받은 accessToken")
    private String accessToken;
}
