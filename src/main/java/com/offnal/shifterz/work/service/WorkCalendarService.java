package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
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
            // 중복 년도,달의 캘린더 체크 (memberId, year, month 중복 체크)
            boolean exists = workCalendarRepository.existsByMemberIdAndYearAndMonth(
                    memberId, unitDto.getYear(), unitDto.getMonth());

            if (exists) {
                throw new CustomException(ErrorCode.CALENDAR_DUPLICATION);
            }

            WorkCalendar calendar = WorkCalendarConverter.toEntity(memberId, workCalendarRequestDto, unitDto);
            WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

            List<WorkInstance> instances = WorkCalendarConverter.toWorkInstances(unitDto, savedCalendar);
            workInstanceRepository.saveAll(instances);
        }

    }

    public List<WorkDayResponseDto> getWorkDaysByYearAndMonth(String year, String month) {

        Long memberId = AuthService.getCurrentUserId();

        List<WorkInstance> instances =
                workInstanceRepository.findByWorkCalendar_MemberIdAndWorkCalendar_YearAndWorkCalendar_Month(memberId, year, month);

        return WorkCalendarConverter.toDayResponseDtoList(instances);
    }

    @Transactional
    public void updateWorkCalendar(String year, String month, WorkCalendarUpdateDto workCalendarUpdateDto) {
        Long memberId = AuthService.getCurrentUserId();

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_NOT_FOUND));

        List<WorkInstance> existingInstance = workInstanceRepository.findByWorkCalendar_MemberIdAndWorkCalendar_YearAndWorkCalendar_Month(memberId, year, month);

        Map<String, WorkInstance> existingMap = existingInstance.stream()
                .collect(Collectors.toMap(WorkInstance::getWorkDay, wi -> wi));

        for(Map.Entry<String, String> entry : workCalendarUpdateDto.getShifts().entrySet()){
            String day = entry.getKey();
            String workType = entry.getValue();

            WorkInstance existing = existingMap.get(day);
            WorkTimeType workTimeType = WorkTimeType.fromSymbol(workType);

            if(existing != null){
                workInstanceRepository.delete(existing);

                WorkInstance newInstance = WorkInstance.builder()
                        .workDay(day)
                        .workTimeType(workTimeType)
                        .workCalendar(calendar)
                        .build();

                workInstanceRepository.save(newInstance);
            }
            else{
                WorkInstance newInstance = WorkInstance.builder()
                        .workDay(day)
                        .workTimeType(workTimeType)
                        .workCalendar(calendar)
                        .build();

                workInstanceRepository.save(newInstance);
            }
        }
    }

    @Transactional
    public void deleteWorkCalendar(String year, String month) {
        Long memberId = AuthService.getCurrentUserId();

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndYearAndMonth(memberId, year, month)
                .orElseThrow(() -> new CustomException(ErrorCode.CALENDAR_NOT_FOUND));

        workInstanceRepository.deleteAllByWorkCalendar(calendar);
        workCalendarRepository.delete(calendar);
    }

}
