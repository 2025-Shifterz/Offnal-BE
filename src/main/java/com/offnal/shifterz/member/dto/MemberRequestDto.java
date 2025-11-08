package com.offnal.shifterz.member.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


public class MemberRequestDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MemberUpdateRequestDto {
        private String email;
        private String  phoneNumber;
        private String name;
        private String profileImageUrl;
    }
}
