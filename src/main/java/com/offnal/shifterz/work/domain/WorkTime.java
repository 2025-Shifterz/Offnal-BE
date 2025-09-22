package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.work.service.WorkCalendarService;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Duration;
import java.time.LocalTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class WorkTime extends BaseTimeEntity {

    @Column(name = "time_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkTimeType timeType;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private Duration duration;


}