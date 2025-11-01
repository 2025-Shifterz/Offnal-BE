package com.offnal.shifterz.organization.converter;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.dto.OrganizationRequestDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;

public class OrganizationConverter {

    public static Organization toEntity(OrganizationRequestDto.CreateDto request, Long memberId){
        return Organization.builder()
                .organizationName(request.getOrganizationName())
                .organizationMember(Member.builder().id(memberId).build())
                .team(request.getTeam())
                .build();
    }

    public static OrganizationResponseDto.OrganizationDto toDto(Organization organization){
        return OrganizationResponseDto.OrganizationDto.builder()
                .id(organization.getId())
                .organizationName(organization.getOrganizationName())
                .team(organization.getTeam())
                .build();
    }
}
