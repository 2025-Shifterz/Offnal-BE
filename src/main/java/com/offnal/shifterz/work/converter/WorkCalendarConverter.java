package com.offnal.shifterz.work.converter;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.dto.*;

import java.time.Duration;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkCalendarConverter {

    // WorkCalendarRequestDto -> WorkCalendar
    public static WorkCalendar toEntity(Long memberId, Organization organization, WorkCalendarRequestDto workCalendarRequestDto, WorkCalendarUnitDto unitDto) {

        Map<String, WorkTime> workTimeMap = new HashMap<>();
        for (Map.Entry<String, WorkTimeDto> entry : workCalendarRequestDto.getWorkTimes().entrySet()) {
            String symbol = entry.getKey();
            WorkTimeDto workTimeDto = entry.getValue();

            WorkTimeType timeType = WorkTimeType.fromSymbol(symbol);
            LocalTime startTime = LocalTime.parse(workTimeDto.getStartTime());

            Duration duration = workTimeDto.getDuration();
            WorkTime workTime = WorkTime.builder()
                    .timeType(timeType)
                    .startTime(startTime)
                    .duration(duration)
                    .build();
            workTimeMap.put(symbol, workTime);
        }

        return WorkCalendar.builder()
                .memberId(memberId)
                .organization(organization)
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
                .map(WorkCalendarConverter::toDayResponseDto)
                .toList();
    }

    public static WorkDayResponseDto toDayResponseDto(WorkInstance instance) {
        WorkTime workTime = resolveWorkTimeFor(instance);

        LocalTime startTime = (workTime != null) ? workTime.getStartTime() : null;
        Duration duration = (workTime != null) ? workTime.getDuration() : null;

        return WorkDayResponseDto.builder()
                .date(instance.getWorkDate())
                .workTypeName(instance.getWorkTimeType().getKoreanName())
                .startTime(startTime)
                .duration(duration)
                .build();
    }

    public static WorkCalendarMetaDto toMetaDto(WorkCalendar cal) {
        return WorkCalendarMetaDto.builder()
                .calendarId(cal.getId())
                .workTimes(toSymbolKeyWorkTimes(cal))
                .build();
    }

    public static WorkCalendarListItemDto toListItemDto(WorkCalendar c) {
        return WorkCalendarListItemDto.builder()
                .calendarId(c.getId())
                .workTimes(toSymbolKeyWorkTimes(c))
                .build();
    }


    public static WorkTime resolveWorkTimeFor(WorkInstance instance) {
        if (instance == null || instance.getWorkCalendar() == null)
            return null;

        Map<String, WorkTime> workTimes = instance.getWorkCalendar().getWorkTimes();

        if (workTimes == null || instance.getWorkTimeType() == null)
            return null;

        WorkTimeType type = instance.getWorkTimeType();
        WorkTime found = workTimes.get(type.getSymbol());

        if (found == null) {
            found = workTimes.get(type.name());
        }

        return found;
    }

    public static WorkCalendar emptyCalendar(Long memberId, Organization org, Map<String, WorkTime> defaultWorkTimes) {
        return WorkCalendar.builder()
                .memberId(memberId)
                .organization(org)
                .workTimes(defaultWorkTimes)
                .build();
    }


    private static Map<String, WorkTimeDto> toSymbolKeyWorkTimes(WorkCalendar cal) {
        return cal.workTimes().values().stream()
                .collect(Collectors.toMap(
                        wt -> wt.getTimeType().getSymbol(),
                        WorkCalendarConverter::toDto,
                        (a, b) -> a
                ));
    }

    private static WorkTimeDto toDto(WorkTime wt) {
        return new WorkTimeDto(
                wt.getStartTime() == null ? null : wt.getStartTime().toString(),
                wt.getDuration()
        );
    }
}
