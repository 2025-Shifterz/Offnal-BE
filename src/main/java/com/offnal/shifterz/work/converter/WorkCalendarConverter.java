package com.offnal.shifterz.work.converter;

import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;

import java.util.List;
import java.util.stream.Collectors;

public class WorkCalendarConverter {

    public static List<WorkDayResponseDto> toDayResponseDtoList(List<WorkInstance> instances) {
        return instances.stream()
                .map(instance -> WorkDayResponseDto.builder()
                        .day(String.valueOf(Integer.parseInt(instance.getWorkDay())))
                        .workTypeName(instance.getWorkTimeType().getKoreanName())
                        .build())
                .collect(Collectors.toList());
    }
}
