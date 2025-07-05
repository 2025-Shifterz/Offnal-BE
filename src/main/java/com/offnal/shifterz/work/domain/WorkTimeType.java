package com.offnal.shifterz.work.domain;

public enum WorkTimeType {
    DAY,    // 주간
    EVENING, // 오후
    NIGHT,   // 야간
    OFF; // 휴무

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