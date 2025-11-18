package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.member.service.MemberService.MemberErrorCode;
import com.offnal.shifterz.memberOrganizationTeam.service.MyTeamService;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import com.offnal.shifterz.organization.service.OrganizationService;
import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.dto.*;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collection;
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
    private final MemberRepository memberRepository;
    private final MyTeamService myTeamService;

    // 조직 생성 또는 조회 + 근무표 및 근무 일정 등록
    @Transactional
    public void saveWorkCalendar(@Valid WorkCalendarRequestDto workCalendarRequestDto) {

        Long memberId = AuthService.getCurrentUserId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 본인 근무조 (없으면 null/빈값일 수 있음)
        String myTeam = workCalendarRequestDto.getMyTeam();

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

            validateShiftDateRange(unitDto);

            WorkCalendar calendar = WorkCalendarConverter.toEntity(memberId, org, workCalendarRequestDto, unitDto);
            WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

            List<WorkInstance> instances = WorkCalendarConverter.toWorkInstances(unitDto, savedCalendar);
            workInstanceRepository.saveAll(instances);
        }
        // 2) 근무표 저장 후, 나의 근무조 저장 (이제 조직이 DB에 존재함!)
        Set<String> organizationNames = workCalendarRequestDto.getCalendars().stream()
                .map(WorkCalendarUnitDto::getOrganizationName)
                .collect(Collectors.toSet());

        for (String orgName : organizationNames) {

            List<Organization> orgList =
                    organizationRepository.findAllByOrganizationMember_IdAndOrganizationName(memberId, orgName);

            if (!orgList.isEmpty() && myTeam != null && !myTeam.isBlank()) {
                myTeamService.saveOrUpdateMyTeam(member, orgList.get(0), myTeam);
            }
        }
    }

    // shift 날짜가 캘린더 범위안에 있는지 검증
    private void validateShiftDateRange(WorkCalendarUnitDto unitDto) {
        LocalDate start = unitDto.getStartDate();
        LocalDate end = unitDto.getEndDate();

        for (LocalDate date : unitDto.getShifts().keySet()) {

            if (date.isBefore(start) || date.isAfter(end)) {
                throw new CustomException(WorkCalendarErrorCode.INVALID_SHIFT_DATE);
            }
        }
    }

    // 기간으로 근무 일정 조회
    public List<WorkDayResponseDto> getWorkInstancesByRange(String organizationName, String team, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOrganization(memberId, organizationName, team);

        List<WorkInstance> instances =
                workInstanceRepository
                    .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                            memberId, org, startDate, endDate);

        return WorkCalendarConverter.toDayResponseDtoList(instances);
    }

    // 근무 일정 월 단위 조회
    public List<WorkDayResponseDto> getMonthlyWorkInstances(String organizationName, String team, int year, int month) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOrganization(memberId, organizationName, team);

        List<WorkInstance> list =
                workInstanceRepository
                        .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                                memberId, org, startDate, endDate);
        return WorkCalendarConverter.toDayResponseDtoList(list);
    }

    // 근무 일정 수정. 없으면 생성(upsert)
    @Transactional
    public void updateWorkCalendar(String organizationName, String team, WorkCalendarUpdateDto workCalendarUpdateDto) {
        Long memberId = AuthService.getCurrentUserId();

        Map<LocalDate, String> shifts = nonEmptyShifts(workCalendarUpdateDto.getShifts());
        LocalDate minDay = shifts.keySet().stream().min(LocalDate::compareTo)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.SHIFTS_NOT_FOUND));
        LocalDate maxDay = shifts.keySet().stream().max(LocalDate::compareTo)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.SHIFTS_NOT_FOUND));

        Organization org = findOrganization(memberId, organizationName, team);

        WorkCalendar calendar = findIntersectingCalendar(memberId, org, minDay, maxDay);

        expandCalendarRange(calendar, minDay, maxDay);

        // 캘린더 범위 내 근무 일정 조회
        List<WorkInstance> existingInstances = fetchInstancesForCalendarRange(memberId, org, calendar);

        Map<LocalDate, WorkInstance> existingMap = existingInstances.stream()
                .collect(Collectors.toMap(WorkInstance::date, wi -> wi, (a, b) -> a));

        upsertInstances(calendar, memberId, org, shifts, existingMap);
    }

    // 일정이 하나라도 포함된 캘린더 조회
    private WorkCalendar findIntersectingCalendar(Long memberId, Organization org, LocalDate minDay, LocalDate maxDay) {
        return workCalendarRepository
                .findByMemberIdAndOrganizationAndEndDateGreaterThanEqualAndStartDateLessThanEqual(
                        memberId, org, minDay, maxDay
                )
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));
    }

    // 근무 시간 수정
    @Transactional
    public void updateWorkTimes(String organizationName, String team, Long calendarId, @Valid WorkTimeUpdateDto request) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOrganization(memberId, organizationName, team);

        WorkCalendar calendar = findCalendarById(memberId, org, calendarId);

        if (request == null || request.getWorkTimes() == null || request.getWorkTimes().isEmpty()) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_WORK_TIME_REQUIRED);
        }

        Map<String, WorkTime> workTimes = calendar.workTimes();
        if (workTimes  == null || workTimes.isEmpty()) {
            throw new CustomException(WorkCalendarErrorCode.WORK_TIME_NOT_FOUND);
        }

        for (Map.Entry<WorkTimeType, WorkTimeDto> entry : request.getWorkTimes().entrySet()) {
            WorkTimeType type = entry.getKey();
            WorkTimeDto dto = entry.getValue();

            if (dto == null) {
                throw new CustomException(WorkCalendarErrorCode.CALENDAR_WORK_TIME_REQUIRED);
            }

            String workType = type.getSymbol();
            WorkTime target = workTimes.get(workType);

            if (target == null) {
                throw new CustomException(WorkCalendarErrorCode.WORK_TIME_NOT_FOUND);
            }

            LocalTime startTime = parseStartTime(dto.getStartTime());
            Duration duration = validateDuration(dto.getDuration());

            WorkTime updated = WorkTime.builder()
                    .timeType(type)
                    .startTime(startTime)
                    .duration(duration)
                    .build();

            calendar.putWorkTime(workType, updated);
        }

        workCalendarRepository.save(calendar);
    }

    // 근무 일정 삭제
    @Transactional
    public void deleteWorkInstances(String organizationName, String team, LocalDate startDate, LocalDate endDate) {
        validateDateRange(startDate, endDate);
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOrganization(memberId, organizationName, team);

        WorkCalendar calendar = findCalendarByRange(memberId, org, startDate, endDate);

        List<WorkInstance> instances = workInstanceRepository
                .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                        memberId, org, startDate, endDate);

        if (instances.isEmpty()) {
            throw new CustomException(WorkCalendarErrorCode.WORK_INSTANCE_NOT_FOUND);
        }

        workInstanceRepository.deleteAllInBatch(instances);
    }

    // 근무표 삭제
    @Transactional
    public void deleteWorkCalendar(String organizationName, String team, Long calendarId) {
        Long memberId = AuthService.getCurrentUserId();

        Organization org = findOrganization(memberId, organizationName, team);

        WorkCalendar calendar = findCalendarById(memberId, org, calendarId);

        workInstanceRepository.deleteByWorkCalendarId(calendar.getId());

        workCalendarRepository.delete(calendar);
    }

    // 특정 캘린더 메타 정보 및 근무시간 조회
    public WorkCalendarMetaDto getWorkCalendarMeta(String organizationName, String team, Long calendarId) {
        Long memberId = AuthService.getCurrentUserId();
        Organization org = findOrganization(memberId, organizationName, team);
        WorkCalendar cal = findCalendarById(memberId, org, calendarId);

        return WorkCalendarConverter.toMetaDto(cal);
    }

    // 조직 내 모든 캘린더 메타 정보 및 근무시간 조회
    public List<WorkCalendarListItemDto> listWorkCalendars(String organizationName, String team) {
        Long memberId = AuthService.getCurrentUserId();
        Organization org = findOrganization(memberId, organizationName, team);

        List<WorkCalendar> calendars = workCalendarRepository
                .findByMemberIdAndOrganizationOrderByStartDateDesc(memberId, org);

        return toListItemDtos(calendars);
    }

    // 회원의 조직 중 organizationName이 같은 조직의 근무 일정 조회
    public SameOrganizationWorkResDto getSameOrganizationNameWork(
            String organizationName,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Long memberId = AuthService.getCurrentUserId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        List<Organization> organizations = findOrganizationsWithSameName(memberId, organizationName);

        List<TeamWorkInstanceResDto> teamResponses = organizations.stream()
                .map(org -> {
                    List<TeamWorkInstanceResDto.WorkInstanceDto> workInstances =
                            mapWorkInstances(org, startDate, endDate);
                    return toTeamWorkInstanceDto(org, workInstances);
                })
                .toList();

        String myTeam = myTeamService.getMyTeamForOrganization(member, organizationName);

        return SameOrganizationWorkResDto.builder()
                .myTeam(myTeam)
                .teams(teamResponses)
                .build();
    }

    private TeamWorkInstanceResDto toTeamWorkInstanceDto(Organization org, List<TeamWorkInstanceResDto.WorkInstanceDto> workInstances) {
        return TeamWorkInstanceResDto.builder()
                .team(org.getTeam())
                .workInstances(workInstances)
                .build();
    }

    private List<TeamWorkInstanceResDto.WorkInstanceDto> mapWorkInstances(Organization org, LocalDate startDate, LocalDate endDate) {
        List<WorkInstance> instances =
                workInstanceRepository.findByOrganizationIdAndDateRange(
                        org.getId(), startDate, endDate
                );

        return instances.stream()
                .map(i -> {

                    WorkTime workTime = WorkCalendarConverter.resolveWorkTimeFor(i);

                    LocalTime startTime = (workTime != null) ? workTime.getStartTime() : null;
                    Duration duration   = (workTime != null) ? workTime.getDuration()   : null;

                    return TeamWorkInstanceResDto.WorkInstanceDto.builder()
                            .date(i.getWorkDate())
                            .workType(i.getWorkTimeType().getKoreanName())
                            .startTime(startTime)
                            .duration(duration)
                            .build();
                })
                .toList();
    }

    private List<Organization> findOrganizationsWithSameName(Long memberId, String organizationName) {
        List<Organization> organizations =
                organizationRepository.findAllByOrganizationMember_IdAndOrganizationNameOrderByIdAsc(
                        memberId,
                        organizationName
                );

        if (organizations.isEmpty()) {
            throw new CustomException(OrganizationService.OrganizationErrorCode.ORGANIZATION_NOT_FOUND);
        }
        return organizations;
    }


    // ===== private =====

    private List<WorkCalendarListItemDto> toListItemDtos(List<WorkCalendar> calendars) {
        return calendars.stream()
                .map(WorkCalendarConverter::toListItemDto)
                .toList();
    }

    // 시작일/종료일 범위 유효성 검증
    private void validateDateRange(LocalDate start, LocalDate end) {
        if (start == null || end == null) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_DATE_REQUIRED);
        }
        if (end.isBefore(start)) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_INVALID_DATE_RANGE);
        }
    }

    // 사용자, 조직 이름, 조 이름으로 해당 Organization 조회. 없으면 exception.
    private Organization findOrganization(Long memberId, String organizationName, String team) {
        String name = normalize(organizationName);
        String teamName = normalize(team);

        return organizationRepository
                .findByOrganizationMember_IdAndOrganizationNameAndTeam(memberId, name, teamName)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_ORGANIZATION_NOT_FOUND));
    }

    // 사용자, 조직을 기준으로 해당 기간이 포함된 근무표 찾아 반환. 없으면 exception.
    private WorkCalendar findCalendarByRange(Long memberId, Organization org, LocalDate start, LocalDate end) {
        return workCalendarRepository
                .findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
                        memberId, org, start, end)
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));
    }

    // 근무표 이름으로 조회.
    // 사용자, 조직 이름, 팀 이름, 근무표 이름으로 조회. 없으면 exception
    private WorkCalendar findCalendarById(Long memberId, Organization org, Long calendarId) {
        return workCalendarRepository
                .findByIdAndMemberIdAndOrganization(
                        calendarId, memberId, org
                )
                .orElseThrow(() -> new CustomException(WorkCalendarErrorCode.CALENDAR_NOT_FOUND));
    }

    // 근무일정(shifts) null 또는 비어있을 경우 exception
    private Map<LocalDate, String> nonEmptyShifts(Map<LocalDate, String> shifts) {
        if (shifts == null || shifts.isEmpty()) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_SHIFT_REQUIRED);
        }
        return shifts;
    }

    // 모든 날짜가 캘린더의 기간 범위 안에 포함되는지 검증.
    // 근무표의 startDate ~ endDate 범위를 벗어나면 exception
    private void validateAllDatesWithin(WorkCalendar calendar, Collection<LocalDate> dates) {
        for (LocalDate d : dates) {
            if (!calendar.contains(d)) {
                throw new CustomException(WorkCalendarErrorCode.CALENDAR_INVALID_DATE_RANGE);
            }
        }
    }

    // 근무 일정 (workInstance) 조회
    // 사용자, 조직, 캘린더 기간(start~end) 기준으로 해당하는 모든 근무일 조회
    private List<WorkInstance> fetchInstancesForCalendarRange(Long memberId, Organization org, WorkCalendar calendar) {
        return workInstanceRepository
                .findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
                        memberId, org, calendar.startDate(), calendar.endDate());
    }

    // startTime 형식 검증
    private LocalTime parseStartTime(String startTime) {
        try {
            return LocalTime.parse(startTime);
        } catch (Exception e) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_START_TIME_INVALID);
        }
    }

    // 근무 일정 업서트(Upsert)
    // 기존 근무 일정이 있으면 변경, 없으면 생성.
    private void upsertInstances(
            WorkCalendar calendar, Long memberId, Organization org, Map<LocalDate, String> shifts, Map<LocalDate, WorkInstance> existingMap
    ) {
        List<WorkInstance> toCreate = new ArrayList<>();
        List<WorkInstance> toUpdate = new ArrayList<>();

        for (Map.Entry<LocalDate, String> e : shifts.entrySet()) {
            LocalDate day = e.getKey();
            WorkTimeType target = WorkTimeType.fromSymbol(e.getValue());
            WorkInstance cur = existingMap.get(day);

            if (cur == null) {
                toCreate.add(WorkInstance.create(calendar, day, target));
            }
            else if (!cur.isType(target)) {
                cur.changeType(target, memberId, org);
                toUpdate.add(cur);
            }
        }

        List<WorkInstance> total = new ArrayList<>();
        total.addAll(toCreate);
        total.addAll(toUpdate);

        if (!total.isEmpty()) {
            workInstanceRepository.saveAll(total);
        }
    }

    // 근무 시간(Duration) 유효성 검증
    // null, 0분 이하, 24시간 초과일 경우 exception.
    private Duration validateDuration(Duration duration) {
        if (duration == null) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_DURATION_REQUIRED);
        }
        long minutes = duration.toMinutes();
        if (minutes <= 0 || minutes > 24 * 60) {
            throw new CustomException(WorkCalendarErrorCode.CALENDAR_DURATION_REQUIRED);
        }
        return duration;
    }

    // 공백 방지
    private String normalize(String s) {
        return s == null ? null : s.trim();
    }

    // 요청한 근무 일정이 캘린더 범위 초과시 캘린더 범위 확장
    private void expandCalendarRange(WorkCalendar calendar, LocalDate minDay, LocalDate maxDay) {
        if (minDay.isBefore(calendar.startDate())) {
            calendar.updateStartDate(minDay);
        }
        if (maxDay.isAfter(calendar.endDate())) {
            calendar.updateEndDate(maxDay);
        }
    }


    // ===== ErrorCode =====

    @Getter
    @AllArgsConstructor
    public enum WorkCalendarErrorCode implements ErrorReason {
        // 캘린더 저장 관련
        CALENDAR_DUPLICATION("CAL001",HttpStatus.BAD_REQUEST, "이미 존재하는 연도/월의 캘린더입니다."),
        CALENDAR_NAME_REQUIRED("CAL002",HttpStatus.BAD_REQUEST, "근무표 이름은 필수입니다."),
        CALENDAR_START_TIME_INVALID("CAL003",HttpStatus.BAD_REQUEST, "시작 시간이 유효하지 않습니다."),
        CALENDAR_DURATION_REQUIRED("CAL004",HttpStatus.BAD_REQUEST, "근무 소요 시간은 필수입니다."),
        CALENDAR_ORGANIZATION_NOT_FOUND("CAL005",HttpStatus.NOT_FOUND, "존재하지 않는 조직입니다."),
        CALENDAR_WORK_TIME_REQUIRED("CAL006",HttpStatus.BAD_REQUEST, "근무 시간 정보는 필수입니다."),
        CALENDAR_SHIFT_REQUIRED("CAL007",HttpStatus.BAD_REQUEST, "근무 정보는 필수입니다."),


        // 캘린더 수정 관련
        CALENDAR_NOT_FOUND("CAL008",HttpStatus.NOT_FOUND, "해당하는 캘린더를 찾을 수 없습니다."),

        // 캘린더 삭제 관련
        CALENDAR_DELETE_FAILED("CAL009",HttpStatus.BAD_REQUEST, "근무표 삭제에 실패하였습니다."),

        //근무 관련
        WORK_INSTANCE_NOT_FOUND("CAL010",HttpStatus.NOT_FOUND, "해당 일자에 저장된 근무 정보가 없습니다."),
        WORK_TIME_NOT_FOUND("CAL011",HttpStatus.NOT_FOUND, "오늘의 근무 시간 정보가 없습니다."),

        // 근무일 조회 관련
        INVALID_YEAR_FORMAT("CAL012",HttpStatus.BAD_REQUEST, "연도 형식이 올바르지 않습니다."),
        INVALID_MONTH_FORMAT("CAL013",HttpStatus.BAD_REQUEST, "월 형식이 올바르지 않습니다."),
        CALENDAR_DATE_REQUIRED("CAL014",HttpStatus.BAD_REQUEST, "기간을 입력해주세요."),
        CALENDAR_INVALID_DATE_RANGE("CAL015", HttpStatus.BAD_REQUEST, "기간 범위가 올바르지 않습니다."),
        SHIFTS_NOT_FOUND("CAL016", HttpStatus.NOT_FOUND, "존재하는 근무 일정이 없습니다."),

        INVALID_SHIFT_DATE("CAL017", HttpStatus.BAD_REQUEST, "근무 일정이 캘린더의 범위를 벗어났습니다.");
        private final String code;
        private final HttpStatus status;
        private final String message;
    }

}