package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final OrganizationRepository organizationRepository;

    @Transactional
    public void saveWorkCalendar(WorkCalendarRequestDto workCalendarRequestDto, Long organizationId) {

        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        for (WorkCalendarUnitDto unitDto : workCalendarRequestDto.getCalendars()) {
            // 중복 시작일, 종료일의 캘린더 체크 (memberId, startDate, endDate 중복 체크)
            boolean exists = workCalendarRepository.existsByMemberIdAndOrganizationAndStartDateAndEndDate(
                    memberId, org, unitDto.getStartDate(), unitDto.getEndDate());

            if (exists) {
                throw new CustomException(WorkCalendarErrorCode.CALENDAR_DUPLICATION);
            }

            WorkCalendar calendar = WorkCalendarConverter.toEntity(memberId, org, workCalendarRequestDto, unitDto);
            WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

            List<WorkInstance> instances = WorkCalendarConverter.toWorkInstances(unitDto, savedCalendar);
            workInstanceRepository.saveAll(instances);
        }
    }

    public List<WorkDayResponseDto> getWorkDaysByOrganizationAndDateRange(Long organizationId, LocalDate startDate, LocalDate endDate) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));


        List<WorkInstance> instances =
                workInstanceRepository.findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkCalendarStartDateLessThanEqualAndWorkCalendarEndDateGreaterThanEqual(
                        memberId, org, startDate, endDate
                );

        return WorkCalendarConverter.toDayResponseDtoList(instances);
    }

    @Transactional
    public void updateWorkCalendar(Long organizationId, LocalDate startDate, LocalDate endDate, WorkCalendarUpdateDto workCalendarUpdateDto) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        memberId, org, startDate, endDate)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));

        // 캘린더 범위 내 근무 일정 조회
        List<WorkInstance> existingInstances = workInstanceRepository.findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkCalendarStartDateLessThanEqualAndWorkCalendarEndDateGreaterThanEqual(
                memberId, org, startDate, endDate);

        Map<LocalDate, WorkInstance> existingMap = existingInstances.stream()
                .collect(Collectors.toMap(WorkInstance::getWorkDate, wi -> wi));

        for(Map.Entry<LocalDate, String> entry : workCalendarUpdateDto.getShifts().entrySet()){
            LocalDate day = entry.getKey();
            String workType = entry.getValue();

            WorkInstance existing = existingMap.get(day);
            WorkTimeType workTimeType = WorkTimeType.fromSymbol(workType);

            if(existing != null){
                workInstanceRepository.delete(existing);
            }
                WorkInstance newInstance = WorkInstance.builder()
                        .workDate(day)
                        .workTimeType(workTimeType)
                        .workCalendar(calendar)
                        .build();

                workInstanceRepository.save(newInstance);

        }
    }

    @Transactional
    public void deleteWorkCalendar(Long organizationId, LocalDate startDate, LocalDate endDate) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual
                        (memberId, org, startDate, endDate)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));

        workInstanceRepository.deleteAllByWorkCalendar(calendar);
        workCalendarRepository.delete(calendar);
    }

    @Getter
    @AllArgsConstructor
    public enum WorkCalendarErrorCode implements ErrorReason {
        // 캘린더 저장 관련
        CALENDAR_DUPLICATION("CAL001",HttpStatus.BAD_REQUEST, "이미 존재하는 연도/월의 캘린더입니다."),
        CALENDAR_NAME_REQUIRED("CAL002",HttpStatus.BAD_REQUEST, "근무표 이름은 필수입니다."),
        CALENDAR_STARTDAY_REQUIRED("CAL003",HttpStatus.BAD_REQUEST, "시작일은 필수입니다."),
        CALENDAR_DURATION_REQUIRED("CAL004",HttpStatus.BAD_REQUEST, "근무 소요 시간은 필수입니다."),
        CALENDAR_ORGANIZATION_REQUIRED("CAL005",HttpStatus.BAD_REQUEST, "조직은 필수입니다."),
        CALENDAR_WORK_TIME_REQUIRED("CAL006",HttpStatus.BAD_REQUEST, "근무 시간 정보는 필수입니다."),
        CALENDAR_SHIFT_REQUIRED("CAL007",HttpStatus.BAD_REQUEST, "근무일 정보는 필수입니다."),


        // 캘린더 수정 관련
        CALENDAR_NOT_FOUND("CAL008",HttpStatus.NOT_FOUND, "해당하는 연도, 월의 캘린더를 찾을 수 없습니다."),

        // 캘린더 삭제 관련
        CALENDAR_DELETE_FAILED("CAL009",HttpStatus.BAD_REQUEST, "근무표 삭제에 실패하였습니다."),

        //근무 관련
        WORK_INSTANCE_NOT_FOUND("CAL010",HttpStatus.NOT_FOUND, "해당 일자에 저장된 근무 정보가 없습니다."),
        WORK_TIME_NOT_FOUND("CAL011",HttpStatus.NOT_FOUND, "오늘의 근무 시간 정보가 없습니다."),

        // 근무일 조회 관련
        INVALID_YEAR_FORMAT("CAL012",HttpStatus.BAD_REQUEST, "연도 형식이 올바르지 않습니다."),
        INVALID_MONTH_FORMAT("CAL013",HttpStatus.BAD_REQUEST, "월 형식이 올바르지 않습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }

}