package com.offnal.shifterz.home.converter;

import com.offnal.shifterz.home.dto.WorkScheduleResponseDto;
import com.offnal.shifterz.work.domain.WorkTimeType;
import org.springframework.stereotype.Component;

@Component
public class WorkScheduleConverter {

    public WorkScheduleResponseDto toDto(WorkTimeType today) {
        return WorkScheduleResponseDto.builder()
                .todayType(today)
                .build();
    }

}