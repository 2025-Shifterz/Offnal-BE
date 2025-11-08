package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.work.converter.DurationToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class WorkTime extends BaseTimeEntity {

    @Column(name = "time_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private WorkTimeType timeType;

    @Column(nullable = false)
    private LocalTime startTime;

    @Convert(converter = DurationToStringConverter.class)
    @Column(nullable = false)
    private Duration duration;
}