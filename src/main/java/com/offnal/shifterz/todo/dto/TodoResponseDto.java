package com.offnal.shifterz.todo.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public class TodoResponseDto {
    private Long id;
    private String content;
    private Boolean isSuccess;
    private LocalDate targetDate;
    private Long organizationId;
}
