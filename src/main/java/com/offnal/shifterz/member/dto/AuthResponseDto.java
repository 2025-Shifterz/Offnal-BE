package com.offnal.shifterz.member.dto;

import com.offnal.shifterz.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {

    @Schema(description = "회원 이름(소셜 닉네임)", example = "구혜승")
    private String memberName;

    @Schema(description = "회원 이메일", example = "test@example.com")
    private String email;

    @Schema(description = "회원 프로필 이미지 URL")
    private String profileImageUrl;

    @Schema(description = "신규 가입 여부, 기존 - false / 신규 - true", example = "true")
    private boolean newMember;

    @Schema(description = "JWT 액세스 토큰")
    private String accessToken;

    @Schema(description = "JWT 리프레시 토큰")
    private String refreshToken;


    public static AuthResponseDto from(MemberResponseDto.MemberRegisterResponseDto dto,
                                       String accessToken,
                                       String refreshToken) {
        return AuthResponseDto.builder()
                .memberName(dto.getMemberName())
                .email(dto.getEmail())
                .profileImageUrl(dto.getProfileImageUrl())
                .newMember(dto.isNewMember())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
