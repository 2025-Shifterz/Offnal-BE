package com.offnal.shifterz.memo.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

public class MemoResponseDto {

    @Getter
    @Builder
    public static class MemoDto {
        private Long id;
        private String content;         // 메모 내용
        private LocalDate targetDate;   // 메모 날짜 (작성일자/기한)
        private Long organizationId;    // 소속 조직 ID (선택값)
    }
}
