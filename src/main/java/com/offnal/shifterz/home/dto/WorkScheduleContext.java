package com.offnal.shifterz.home.dto;

import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WorkScheduleContext {
    private LocalDate date;
    private WorkTimeType yesterdayType;
    private WorkTimeType todayType;
    private WorkTimeType tomorrowType;
    private WorkTime workTime;
}
