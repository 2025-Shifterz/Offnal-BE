package com.offnal.shifterz.todo.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

public class TodoResponseDto {

    @Data
    @Builder
    public static class TodoDto {
        private Long id;
        private String content;
        private Boolean completed;
        private LocalDate targetDate;
//        private Long memberId;
        private Long organizationId;
    }
}
