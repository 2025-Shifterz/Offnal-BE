package com.offnal.shifterz.member.dto;

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
    public static class MemberUpdateResponseDto {
        private Long id;
        private String email;
        private String memberName;
        private String phoneNumber;
        private String profileImageUrl;
    }

}
