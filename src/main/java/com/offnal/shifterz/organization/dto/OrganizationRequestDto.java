package com.offnal.shifterz.organization.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrganizationRequestDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDto {
        @NotBlank(message = "조직 이름은 필수입니다.")
        private String organizationName;

        @NotBlank(message = "조 이름은 필수입니다.")
        private String team;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDto {
        private String organizationName;
        private String team;
    }
}
