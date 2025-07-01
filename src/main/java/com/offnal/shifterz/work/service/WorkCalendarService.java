package com.offnal.shifterz.work.service;

import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkTimeDto;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkCalendarService {
    private final WorkCalendarRepository workCalendarRepository;
    private final WorkInstanceRepository workInstanceRepository;

    @Transactional
    public Long saveWorkCalendar(WorkCalendarRequestDto workCalendarRequestDto) {
        WorkCalendar calendar = new WorkCalendar();

        calendar.setCalendarName(workCalendarRequestDto.getCalendarName());
        calendar.setYear(workCalendarRequestDto.getYear());
        calendar.setMonth(workCalendarRequestDto.getMonth());
        calendar.setMemberId(workCalendarRequestDto.getMemberId());
        calendar.setWorkGroup(workCalendarRequestDto.getWorkGroup());

        // workTimes -> Map 변환
        Map<String, WorkTime> workTimeMap = new HashMap<>();
        for (Map.Entry<String, WorkTimeDto> entry: workCalendarRequestDto.getWorkTimes().entrySet()){
            String symbol = entry.getKey(); // D, E, N

            WorkTimeType timeType = WorkTimeType.fromSymbol(symbol);
            LocalTime startTime = LocalTime.parse(entry.getValue().getStartTime());
            LocalTime endTime = LocalTime.parse(entry.getValue().getEndTime());

            WorkTime workTime = new WorkTime(timeType, startTime, endTime);
            workTimeMap.put(symbol, workTime);
        }
        calendar.setWorkTimes(workTimeMap);

        WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

        List<WorkInstance> workInstances = new ArrayList<>();
        for (Map.Entry<String, String> entry : workCalendarRequestDto.getShifts().entrySet()){
            String day = entry.getKey();
            String symbol = entry.getValue();

            WorkTimeType timeType = WorkTimeType.fromSymbol(symbol);

            WorkInstance workInstance = new WorkInstance();
            workInstance.setWorkDay(day);
            workInstance.setWorkTimeType(timeType);
            workInstance.setWorkCalendar(savedCalendar);

            workInstances.add(workInstance);
        }

        workInstanceRepository.saveAll(workInstances);

        return savedCalendar.getId();
    }
}
