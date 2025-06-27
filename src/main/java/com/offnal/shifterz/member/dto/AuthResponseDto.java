package com.offnal.shifterz.member.dto;

import com.offnal.shifterz.member.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDto {

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "신규 가입 여부")
    private boolean isNewMember;

    @Schema(description = "응답 메시지")
    private String message;


    public static AuthResponseDto from(Member member, boolean isNewMember) {
        return AuthResponseDto.builder()
                .nickname(member.getKakaoNickname())
                .isNewMember(isNewMember)
                .message("로그인을 성공했습니다")
                .build();
    }
}