package com.offnal.shifterz.work.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkTime {

    @Enumerated(EnumType.STRING)
    private WorkTimeType timeType;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}