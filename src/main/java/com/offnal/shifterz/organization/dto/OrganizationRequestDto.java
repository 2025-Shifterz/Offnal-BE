package com.offnal.shifterz.organization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

public class OrganizationRequestDto {

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDto {
        @NotBlank(message = "조직 이름은 필수입니다.")
        private String organizationName;

        private String team;
    }

    @Data
    @ToString
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDto {
        private String organizationName;
        private String team;
    }
}
