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

    private String message;

}
