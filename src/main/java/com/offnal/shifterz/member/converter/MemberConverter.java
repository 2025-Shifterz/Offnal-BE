package com.offnal.shifterz.member.converter;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.dto.MemberResponseDto;

public class MemberConverter {
    public static MemberResponseDto.MemberUpdateResponseDto toResponse(Member member) {
        return MemberResponseDto.MemberUpdateResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .phoneNumber(member.getPhoneNumber())
                .profileImageUrl(member.getProfileImageUrl())
                .build();
    }
}
