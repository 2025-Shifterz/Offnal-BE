package com.offnal.shifterz.organization.dto;

import lombok.*;


public class OrganizationResponseDto {
    @Data
    @ToString
    @Builder
    @AllArgsConstructor
    public static class OrganizationDto{
        private Long id;
        private String organizationName;
        private String team;
    }
}
