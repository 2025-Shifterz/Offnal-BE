package com.offnal.shifterz.work.domain;

public enum WorkTimeType {
    DAY("주간"),
    EVENING("오후"),
    NIGHT("야간"),
    OFF("휴무");

    private final String koreanName;

    WorkTimeType(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }
}