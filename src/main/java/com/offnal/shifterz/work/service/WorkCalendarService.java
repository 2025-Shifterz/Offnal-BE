package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkCalendarUnitDto;
import com.offnal.shifterz.work.dto.WorkCalendarUpdateDto;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkCalendarService {
    private final WorkCalendarRepository workCalendarRepository;
    private final WorkInstanceRepository workInstanceRepository;

    @Transactional
    public void saveWorkCalendar(WorkCalendarRequestDto workCalendarRequestDto) {

        Long memberId = AuthService.getCurrentUserId();

        for (WorkCalendarUnitDto unitDto : workCalendarRequestDto.getCalendars()) {
            // 중복 시작일, 종료일의 캘린더 체크 (memberId, startDate, endDate 중복 체크)
            boolean exists = workCalendarRepository.existsByMemberIdAndStartDateAndEndDate(
                    memberId, unitDto.getStartDate(), unitDto.getEndDate());

            if (exists) {
                throw new CustomException(ErrorCode.CALENDAR_DUPLICATION);
            }

            WorkCalendar calendar = WorkCalendarConverter.toEntity(memberId, workCalendarRequestDto, unitDto);
            WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

            List<WorkInstance> instances = WorkCalendarConverter.toWorkInstances(unitDto, savedCalendar);
            workInstanceRepository.saveAll(instances);
        }

    }

    public List<WorkDayResponseDto> getWorkDaysByStartDateAndEndDate(LocalDate startDate, LocalDate endDate) {

        Long memberId = AuthService.getCurrentUserId();

        List<WorkInstance> instances =
                workInstanceRepository.findByWorkCalendarMemberIdAndWorkCalendarStartDateLessThanEqualAndWorkCalendarEndDateGreaterThanEqual(memberId, startDate, endDate);

        return WorkCalendarConverter.toDayResponseDtoList(instances);
    }

    @Transactional
    public void updateWorkCalendar(LocalDate startDate, LocalDate endDate, WorkCalendarUpdateDto workCalendarUpdateDto) {
        Long memberId = AuthService.getCurrentUserId();

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndStartDateAndEndDate(memberId, startDate, endDate)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_NOT_FOUND));

        // 캘린더 범위 내 근무 일정 조회
        List<WorkInstance> existingInstances = workInstanceRepository.findByWorkCalendarMemberIdAndWorkCalendarStartDateLessThanEqualAndWorkCalendarEndDateGreaterThanEqual(memberId, startDate, endDate);

        Map<LocalDate, WorkInstance> existingMap = existingInstances.stream()
                .collect(Collectors.toMap(WorkInstance::getWorkDate, wi -> wi));

        for(Map.Entry<LocalDate, String> entry : workCalendarUpdateDto.getShifts().entrySet()){
            LocalDate day = entry.getKey();
            String workType = entry.getValue();

            WorkInstance existing = existingMap.get(day);
            WorkTimeType workTimeType = WorkTimeType.fromSymbol(workType);

            if(existing != null){
                workInstanceRepository.delete(existing);

                WorkInstance newInstance = WorkInstance.builder()
                        .workDate(day)
                        .workTimeType(workTimeType)
                        .workCalendar(calendar)
                        .build();

                workInstanceRepository.save(newInstance);
            }
            else{
                WorkInstance newInstance = WorkInstance.builder()
                        .workDate(day)
                        .workTimeType(workTimeType)
                        .workCalendar(calendar)
                        .build();

                workInstanceRepository.save(newInstance);
            }
        }
    }

    @Transactional
    public void deleteWorkCalendar(LocalDate startDate, LocalDate endDate) {
        Long memberId = AuthService.getCurrentUserId();

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndStartDateAndEndDate(memberId, startDate, endDate)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_NOT_FOUND));

        workInstanceRepository.deleteAllByWorkCalendar(calendar);
        workCalendarRepository.delete(calendar);
    }

}
