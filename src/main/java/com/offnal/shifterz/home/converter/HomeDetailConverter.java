package com.offnal.shifterz.home.converter;

import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.HomeDetailResponseDto;
import com.offnal.shifterz.work.domain.WorkTimeType;
import org.springframework.stereotype.Component;

@Component
public class HomeDetailConverter {

    public HomeDetailResponseDto toDto(
            WorkTimeType yesterday,
            WorkTimeType today,
            WorkTimeType tomorrow,
            DailyRoutineResDto routine
    ) {
        return HomeDetailResponseDto.builder()
                .yesterdayType(yesterday)
                .todayType(today)
                .tomorrowType(tomorrow)
                .todayRoutine(routine)
                .build();
    }
}