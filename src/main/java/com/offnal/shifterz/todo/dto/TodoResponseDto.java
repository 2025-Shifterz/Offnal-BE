package com.offnal.shifterz.todo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class TodoResponseDto {

    @Getter
    @Builder
    public static class TodoDto {
        private Long id;
        private String content;
        private Boolean isSuccess;
        private LocalDate targetDate;
//        private Long memberId;
        private Long organizationId;
    }
}
