package com.offnal.shifterz.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class MemberRequestDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "회원 프로필 수정 요청 DTO")
    public static class MemberUpdateRequestDto {
        @Schema(example = "test@offnal.com", description = "이메일 주소")
        private String email;

        @Schema(example = "010-1111-1111", description = "전화번호")
        private String  phoneNumber;

        @Schema(example = "테스트", description = "회원 이름")
        private String name;

        @Schema(hidden = true)
        private String profileImageUrl;
    }
}
