package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkTime {

    @Column(name = "time_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkTimeType timeType;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Duration duration;


    // 유효성 검증
    public static WorkTime of(WorkTimeType type, LocalTime start, Duration duration) {
        if (start == null) {
            throw new CustomException(ErrorCode.CALENDAR_WORK_TIME_REQUIRED);
        }
        if (duration == null) {
            throw new CustomException(ErrorCode.CALENDAR_WORK_TIME_REQUIRED);
        }
        return new WorkTime(type, start, duration);
    }
}