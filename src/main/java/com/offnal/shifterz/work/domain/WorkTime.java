package com.offnal.shifterz.work.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkTime {

    @Column(name = "time_type")
    @Enumerated(EnumType.STRING)
    private WorkTimeType timeType;

    private LocalTime startTime;

    private LocalTime endTime;
}