package com.offnal.shifterz.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


public class OrganizationResponseDto {
    @Data
    @Builder
    @AllArgsConstructor
    public static class OrganizationDto{
        private Long id;
        private String organizationName;
        private String team;
    }
}
