package com.offnal.shifterz.work.converter;

import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkTimeDto;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkCalendarConverter {

    // WorkCalendarRequestDto -> WorkCalendar
    public static WorkCalendar toEntity(WorkCalendarRequestDto workCalendarRequestDto) {

        Map<String, WorkTime> workTimeMap = new HashMap<>();
        for (Map.Entry<String, WorkTimeDto> entry : workCalendarRequestDto.getWorkTimes().entrySet()) {
            String symbol = entry.getKey();
            WorkTimeDto workTimeDto = entry.getValue();

            WorkTimeType timeType = WorkTimeType.fromSymbol(symbol);
            LocalTime startTime = LocalTime.parse(workTimeDto.getStartTime());
            LocalTime endTime = LocalTime.parse(workTimeDto.getEndTime());

            WorkTime workTime = new WorkTime(timeType, startTime, endTime);
            workTimeMap.put(symbol, workTime);
        }

        return WorkCalendar.builder()
                .calendarName(workCalendarRequestDto.getCalendarName())
                .year(workCalendarRequestDto.getYear())
                .month(workCalendarRequestDto.getMonth())
                .memberId(workCalendarRequestDto.getMemberId())
                .workGroup(workCalendarRequestDto.getWorkGroup())
                .workTimes(workTimeMap)
                .build();
    }

    // WorkCalendarRequestDto -> List<WorkInstance>
    public static List<WorkInstance> toWorkInstances(WorkCalendarRequestDto workCalendarRequestDto, WorkCalendar calendar) {
        return workCalendarRequestDto.getShifts().entrySet().stream()
                .map(entry -> WorkInstance.builder()
                        .workDay(entry.getKey())
                        .workTimeType(WorkTimeType.fromSymbol(entry.getValue()))
                        .workCalendar(calendar)
                        .build())
                .toList();

    }
}
