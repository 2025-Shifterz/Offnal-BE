package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkTimeDto;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WorkCalendarService {
    private final WorkCalendarRepository workCalendarRepository;
    private final WorkInstanceRepository workInstanceRepository;

    // 유효성 검사
    private void validate(WorkCalendarRequestDto workCalendarRequestDto){

        if (workCalendarRequestDto.getCalendarName() == null || workCalendarRequestDto.getCalendarName().isBlank()) {
            throw new CustomException(ErrorCode.CALENDAR_NAME_REQUIRED);
        }
        if (workCalendarRequestDto.getYear() == null || workCalendarRequestDto.getYear().isBlank()) {
            throw new CustomException(ErrorCode.CALENDAR_YEAR_REQUIRED);
        }
        if (workCalendarRequestDto.getMonth() == null || workCalendarRequestDto.getMonth().isBlank()) {
            throw new CustomException(ErrorCode.CALENDAR_MONTH_REQUIRED);
        }
        if (workCalendarRequestDto.getWorkGroup() == null || workCalendarRequestDto.getWorkGroup().isBlank()) {
            throw new CustomException(ErrorCode.CALENDAR_WORK_GROUP_REQUIRED);
        }
        if (workCalendarRequestDto.getWorkTimes() == null || workCalendarRequestDto.getWorkTimes().isEmpty()){
            throw new CustomException(ErrorCode.CALENDAR_WORK_TIME_REQUIRED);
        }
        for (Map.Entry<String, WorkTimeDto> entry : workCalendarRequestDto.getWorkTimes().entrySet()) {
            WorkTimeDto workTimeDto = entry.getValue();
            if (workTimeDto == null
                    || workTimeDto.getStartTime() == null || workTimeDto.getStartTime().isBlank()
                    || workTimeDto.getEndTime() == null || workTimeDto.getEndTime().isBlank()) {
                throw new CustomException(ErrorCode.CALENDAR_WORK_TIME_REQUIRED);
            }
        }
        if (workCalendarRequestDto.getShifts() == null || workCalendarRequestDto.getShifts().isEmpty()){
            throw new CustomException(ErrorCode.CALENDAR_SHIFT_REQUIRED);
        }
    }

    @Transactional
    public void saveWorkCalendar(WorkCalendarRequestDto workCalendarRequestDto) {
        validate(workCalendarRequestDto);
        Long memberId = AuthService.getCurrentUserId();

        WorkCalendar calendar = WorkCalendarConverter.toEntity(memberId, workCalendarRequestDto);
        WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

        List<WorkInstance> instances = WorkCalendarConverter.toWorkInstances(workCalendarRequestDto, savedCalendar);
        workInstanceRepository.saveAll(instances);
    }
}
