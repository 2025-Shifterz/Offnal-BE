package com.offnal.shifterz.work.converter;

import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkCalendarUnitDto;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;
import com.offnal.shifterz.work.dto.WorkTimeDto;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class WorkCalendarConverter {

    // WorkCalendarRequestDto -> WorkCalendar
    public static WorkCalendar toEntity(Long memberId, WorkCalendarRequestDto workCalendarRequestDto, WorkCalendarUnitDto unitDto) {

        Map<String, WorkTime> workTimeMap = new HashMap<>();
        for (Map.Entry<String, WorkTimeDto> entry : workCalendarRequestDto.getWorkTimes().entrySet()) {
            String symbol = entry.getKey();
            WorkTimeDto workTimeDto = entry.getValue();

            WorkTimeType timeType = WorkTimeType.fromSymbol(symbol);
            LocalTime startTime = LocalTime.parse(workTimeDto.getStartTime());

            String[] hm = workTimeDto.getDuration().split(":");
            Duration duration = Duration.ofHours(Integer.parseInt(hm[0]))
                                        .plusMinutes(Integer.parseInt(hm[1]));

            WorkTime workTime = WorkTime.of(timeType, startTime, duration);
            workTimeMap.put(symbol, workTime);
        }

        return WorkCalendar.builder()
                .calendarName(workCalendarRequestDto.getCalendarName())
                .startDate(unitDto.getStartDate())
                .endDate(unitDto.getEndDate())
                .memberId(memberId)
                .workTimes(workTimeMap)
                .build();
    }

    // WorkCalendarRequestDto -> List<WorkInstance>
    public static List<WorkInstance> toWorkInstances(WorkCalendarUnitDto unitDto, WorkCalendar calendar) {
        return unitDto.getShifts().entrySet().stream()
                .map(entry -> WorkInstance.builder()
                        .workDate(entry.getKey())
                        .workTimeType(WorkTimeType.fromSymbol(entry.getValue()))
                        .workCalendar(calendar)
                        .build())
                .toList();

    }
    public static List<WorkDayResponseDto> toDayResponseDtoList(List<WorkInstance> instances) {
        return instances.stream()
                .map(instance -> WorkDayResponseDto.builder()
                        .date(instance.getWorkDate())
                        .workTypeName(instance.getWorkTimeType().getKoreanName())
                        .build())
                .collect(Collectors.toList());
    }
}
