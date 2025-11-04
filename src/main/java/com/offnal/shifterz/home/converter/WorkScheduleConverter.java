package com.offnal.shifterz.home.converter;

import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.home.dto.WorkScheduleResponseDto;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class WorkScheduleConverter {

    public WorkScheduleResponseDto toDto(WorkTimeType today) {
        return WorkScheduleResponseDto.builder()
                .todayType(today)
                .build();
    }

    public WorkScheduleResponseDto toDto(WorkScheduleContext context) {
        return WorkScheduleResponseDto.builder()
                .todayType(context.getTodayType())
                .build();
    }

    public WorkScheduleContext toContext(
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