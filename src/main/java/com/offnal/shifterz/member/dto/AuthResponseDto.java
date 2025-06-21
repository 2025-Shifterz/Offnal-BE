package com.offnal.shifterz.member.dto;

import com.offnal.shifterz.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {
    @Schema(description = "회원 ID")
    private Long memberId;
    @Schema(description = "이메일")
    private String email;
    @Schema(description = "닉네임")
    private String nickname;
    @Schema(description = "프로필 이미지 URL")
    private String profileImageUrl;
    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;
    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;

    @Schema(description = "신규 가입 여부")
    private boolean isNewMember;

    public static AuthResponseDto from(Member member, String accessToken, String refreshToken, boolean isNewMember) {
        return AuthResponseDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getKakaoNickname())
                .profileImageUrl(member.getProfileImageUrl())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewMember(isNewMember)
                .build();
    }
}
