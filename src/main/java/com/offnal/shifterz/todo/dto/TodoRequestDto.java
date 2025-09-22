package com.offnal.shifterz.todo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class TodoRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDto {
        private String content;
        private Boolean isSuccess;
        private Long targetDate;       // 없으면 오늘 날짜로
        private Long organizationId;   // 선택값 (nullable)
    }
}

