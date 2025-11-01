package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import com.offnal.shifterz.organization.service.OrganizationService;
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
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class WorkCalendarService {
    private final WorkCalendarRepository workCalendarRepository;
    private final WorkInstanceRepository workInstanceRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationService organizationService;

    // 조직 생성 또는 조회 + 근무표 및 근무 일정 등록
    @Transactional
    public void saveWorkCalendar(@Valid WorkCalendarRequestDto workCalendarRequestDto) {

        Long memberId = AuthService.getCurrentUserId();

        for (WorkCalendarUnitDto unitDto : workCalendarRequestDto.getCalendars()) {

            // 조직 + 조 조회 (없으면 생성)
            Organization org = organizationService.getOrCreateByMemberAndNameAndTeam(
                    unitDto.getOrganizationName(),
                    unitDto.getTeam()
            );

            // 중복 확인 (member + org + startDate + endDate)
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

    // 기간으로 근무 일정 조회
    public List<WorkDayResponseDto> getWorkInstancesByRange(Long organizationId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_DATE_REQUIRED);
        }
        if (endDate.isBefore(startDate)) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_INVALID_DATE_RANGE);
        }
        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        List<WorkInstance> instances =
                workInstanceRepository
                    .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                            memberId, org, startDate, endDate);

        return WorkCalendarConverter.toDayResponseDtoList(instances);
    }

    // 근무 일정 월 단위 조회
    public List<WorkDayResponseDto> getMonthlyWorkInstances(Long organizationId, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        List<WorkInstance> list =
                workInstanceRepository
                        .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                                memberId, org, startDate, endDate);
        return WorkCalendarConverter.toDayResponseDtoList(list);
    }

    // 근무 일정 수정. 없으면 생성(upsert)
    @Transactional
    public void updateWorkCalendar(Long organizationId, WorkCalendarUpdateDto workCalendarUpdateDto) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        Map<LocalDate, String> shifts = workCalendarUpdateDto.getShifts();
        if (shifts == null || shifts.isEmpty()) {
            throw new CustomException(WorkCalendarErrorCode.WORK_INSTANCE_NOT_FOUND);
        }

        LocalDate minDay = shifts.keySet().stream().min(LocalDate::compareTo).get();
        LocalDate maxDay = shifts.keySet().stream().max(LocalDate::compareTo).get();

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        memberId, org, minDay, maxDay)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));

        for (LocalDate date : shifts.keySet()) {
            if(!calendar.contains(date)){
                throw new CustomException(WorkCalendarErrorCode.WORK_INSTANCE_NOT_FOUND);
            }
        }

        // 캘린더 범위 내 근무 일정 조회
        List<WorkInstance> existingInstances = workInstanceRepository.findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                memberId, org, calendar.getStartDate(), calendar.getEndDate());

        Map<LocalDate, WorkInstance> existingMap = existingInstances.stream()
                .collect(Collectors.toMap(WorkInstance::getWorkDate, wi -> wi, (a, b) -> a));

        List<WorkInstance> toCreate = new ArrayList<>();
        List<WorkInstance> toUpdate = new ArrayList<>();


        for(Map.Entry<LocalDate, String> entry : shifts.entrySet()) {
            LocalDate day = entry.getKey();
            WorkTimeType target = WorkTimeType.fromSymbol(entry.getValue());
            WorkInstance cur = existingMap.get(day);

            if (cur == null) {
                toCreate.add(WorkInstance.create(calendar, day, target));
            } else if (cur.getWorkTimeType() != target) {
                cur.updateWorkTimeType(target, memberId, org);
                toUpdate.add(cur);
            }
        }
        if (!toUpdate.isEmpty()) workInstanceRepository.saveAll(toUpdate);
        if (!toCreate.isEmpty()) workInstanceRepository.saveAll(toCreate);
    }

    @Transactional
    public void deleteWorkCalendar(Long organizationId, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_DATE_REQUIRED);
        }
        if (endDate.isBefore(startDate)) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_INVALID_DATE_RANGE);
        }
        Long memberId = AuthService.getCurrentUserId();

        Organization org = organizationRepository.findById(organizationId)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_REQUIRED));

        WorkCalendar calendar = workCalendarRepository
                .findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual
                        (memberId, org, startDate, endDate)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));

        List<WorkInstance> instances = workInstanceRepository
                .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                        memberId, org, startDate, endDate);

        if (instances.isEmpty()) {
            throw new CustomException(WorkCalendarErrorCode.WORK_INSTANCE_NOT_FOUND);
        }

        workInstanceRepository.deleteAllInBatch(instances);
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
        INVALID_MONTH_FORMAT("CAL013",HttpStatus.BAD_REQUEST, "월 형식이 올바르지 않습니다."),
        CALENDAR_DATE_REQUIRED("CAL014",HttpStatus.BAD_REQUEST, "기간을 입력해주세요."),
        CALENDAR_INVALID_DATE_RANGE("CAL015", HttpStatus.BAD_REQUEST, "기간 범위가 올바르지 않습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }

}