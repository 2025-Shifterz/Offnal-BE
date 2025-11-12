package com.offnal.shifterz.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

    @Data
    @Builder
    public static class MemberRegisterResponseDto {
        private Long id;
        private String email;
        private String memberName;
        private String phoneNumber;
        private String profileImageUrl;
        private boolean isNewMember;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Schema(description = "회원 프로필 수정 응답 DTO")
    public static class MemberUpdateResponseDto {
        private Long id;

        @Schema(example = "user@example.com", description = "이메일 주소")
        private String email;

        @Schema(example = "홍길동", description = "회원 이름")
        private String memberName;

        @Schema(example = "010-1111-1111", description = "전화번호")
        private String phoneNumber;

        @Schema(hidden = true)
        private String profileImageUrl;
    }

}
