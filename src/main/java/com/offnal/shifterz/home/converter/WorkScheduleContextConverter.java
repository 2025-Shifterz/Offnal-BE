package com.offnal.shifterz.home.converter;

import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class WorkScheduleContextConverter {

    public WorkScheduleContext toContext(
            Long memberId,
            LocalDate date,
            WorkTimeType yesterdayType,
            WorkTimeType todayType,
            WorkTimeType tomorrowType,
            WorkTime workTime
    ) {
        return WorkScheduleContext.builder()
                .date(date)
                .yesterdayType(yesterdayType)
                .todayType(todayType)
                .tomorrowType(tomorrowType)
                .workTime(workTime)
                .build();
    }
}

