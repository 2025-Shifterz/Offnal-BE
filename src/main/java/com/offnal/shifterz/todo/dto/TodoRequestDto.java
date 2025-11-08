package com.offnal.shifterz.todo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


public class TodoRequestDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDto {
        @NotNull(message = "할 일 내용은 필수입니다.")
        private String content;

        @Builder.Default
        private Boolean completed = false;

        @NotNull(message = "목표 날짜는 필수입니다.")
        private LocalDate targetDate;

        private Long organizationId; // 선택 필드
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDto {
        @NotNull(message = "할 일 ID는 필수입니다.")
        private Long id;
        private String content;
        private Boolean completed;
        private LocalDate targetDate;
    }
}

