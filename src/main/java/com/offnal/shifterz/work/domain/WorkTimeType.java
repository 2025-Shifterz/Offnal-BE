package com.offnal.shifterz.work.domain;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "근무 유형", example = "DAY")
public enum WorkTimeType {
    DAY("주간"),
    EVENING("오후"),
    NIGHT("야간"),
    OFF("휴일");

    private final String koreanName;

    WorkTimeType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static WorkTimeType fromSymbol(String symbol) {
        return switch (symbol) {
            case "D" -> DAY;
            case "E" -> EVENING;
            case "N" -> NIGHT;
            case "-" -> OFF;
            default -> throw new IllegalArgumentException("Unknown shift type: " + symbol);
        };
    }
}