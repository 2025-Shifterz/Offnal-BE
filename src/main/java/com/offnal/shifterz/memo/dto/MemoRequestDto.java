package com.offnal.shifterz.memo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemoRequestDto {

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDto {
        @NotNull(message = "메모 내용은 필수입니다.")
        private String content;

        @NotNull(message = "메모 날짜는 필수입니다.")
        private LocalDate targetDate;

        private Long organizationId; // 선택 필드
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemoDto {
        private String content;
        private LocalDate targetDate;
    }
}
