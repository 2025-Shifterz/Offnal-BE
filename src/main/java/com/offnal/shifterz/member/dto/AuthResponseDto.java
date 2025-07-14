package com.offnal.shifterz.member.dto;

import com.offnal.shifterz.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {

    @Schema(description = "카카오 닉네임", example = "구혜승")
    private String nickname;

    @Schema(description = "신규 가입 여부, 기존 - false / 신규 - true", example = "true")
    private boolean newMember;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;

    @Builder
    public AuthResponseDto(String nickname, boolean newMember, String accessToken, String refreshToken) {
        this.nickname = nickname;
        this.newMember = newMember;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public static AuthResponseDto from(Member member, boolean isNewMember, String accessToken, String refreshToken) {
        return AuthResponseDto.builder()
                .nickname(member.getKakaoNickname())
                .newMember(isNewMember)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
