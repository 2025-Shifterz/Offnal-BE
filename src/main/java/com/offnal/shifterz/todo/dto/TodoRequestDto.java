package com.offnal.shifterz.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class TodoRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDto {
        private String content;
        private Boolean isSuccess;
        private LocalDate targetDate;
        private Long organizationId; // 선택 필드
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDto {
        private String content;
        private Boolean isSuccess;
        private LocalDate targetDate;
    }
}

