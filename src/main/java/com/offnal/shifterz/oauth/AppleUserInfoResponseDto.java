package com.offnal.shifterz.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AppleUserInfoResponseDto {

    @JsonProperty("sub")
    private String sub; // Apple 고유 사용자 ID

    @JsonProperty("email")
    private String email;

    @JsonProperty("email_verified")
    private Boolean emailVerified;

    @JsonProperty("is_private_email")
    private Boolean isPrivateEmail;

    // 생성자 추가 (토큰 파싱 후 직접 생성용)
    public AppleUserInfoResponseDto(String sub, String email) {
        this.sub = sub;
        this.email = email;
    }
}