package com.offnal.shifterz.memo.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

public class MemoRequestDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreateDto {

        @NotNull(message = "메모 제목은 필수입니다.")
        private String title;

        @NotNull(message = "메모 내용은 선택입니다.")
        private String content;

        @NotNull(message = "메모 날짜는 필수입니다.")
        private LocalDate targetDate;

        private Long organizationId; // 선택 필드
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateMemoDto {

        @NotNull(message = "메모 ID는 필수입니다.")
        private Long id;
        private String content;
        private LocalDate targetDate;
    }
}
