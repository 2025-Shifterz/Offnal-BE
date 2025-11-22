package com.offnal.shifterz.home.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.home.converter.HomeDetailConverter;
import com.offnal.shifterz.home.converter.WorkScheduleContextConverter;
import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.home.dto.WorkScheduleResponseDto;
import com.offnal.shifterz.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.memberOrganizationTeam.repository.MemberOrganizationTeamRepository;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final WorkScheduleService workScheduleService;
    private final DailyRoutineService dailyRoutineService;
    private final WorkCalendarRepository workCalendarRepository;
    private final WorkScheduleContextConverter contextConverter;
    private final WorkInstanceRepository workInstanceRepository;
    private final MemberOrganizationTeamRepository memberOrganizationTeamRepository;
    private final HomeDetailConverter homeDetailConverter;

    @Transactional(readOnly = true)
    public WorkScheduleResponseDto getWorkSchedule() {
        Long memberId = AuthService.getCurrentUserId();
        LocalDate today = LocalDate.now();

        MemberOrganizationTeam memberOrgTeam = memberOrganizationTeamRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(HomeErrorCode.MY_TEAM_NOT_FOUND));

        Organization org = getMyTeamOrganization(memberId);

        WorkTimeType yesterdayType = findTypeForOrg(org, today.minusDays(1));
        WorkTimeType todayType = findTypeForOrg(org, today);
        WorkTimeType tomorrowType = findTypeForOrg(org, today.plusDays(1));

        WorkTime workTime = getTodayWorkTime(org, today);

        DailyRoutineResDto routine = buildRoutineIfNeeded(
                memberId, today, yesterdayType, todayType, tomorrowType, workTime
        );

        return WorkScheduleResponseDto.builder()
                .yesterdayType(yesterdayType)
                .todayType(todayType)
                .tomorrowType(tomorrowType)
                .build();
    }

    private DailyRoutineResDto buildRoutineIfNeeded(
            Long memberId, LocalDate today, WorkTimeType yesterdayType, WorkTimeType todayType, WorkTimeType tomorrowType, WorkTime workTime
    ) {
        WorkScheduleContext context = contextConverter.toContext(
                memberId, today, yesterdayType, todayType, tomorrowType, workTime
        );

        if (context.getTodayType() == null) {
            return null;
        }

        return dailyRoutineService.buildRoutine(context);
    }

    private WorkTime getTodayWorkTime(Organization org, LocalDate today) {
        return workInstanceRepository
                .findByWorkCalendarOrganizationAndWorkDate(org, today)
                .map(WorkCalendarConverter::resolveWorkTimeFor)
                .orElse(null);
    }

    private WorkTimeType findTypeForOrg(Organization org, LocalDate date) {
        return workInstanceRepository
                .findByWorkCalendarOrganizationAndWorkDate(org, date)
                .map(WorkInstance::getWorkTimeType)
                .orElse(null);
    }

    private Organization getMyTeamOrganization(Long memberId) {
        return memberOrganizationTeamRepository
                .findByMemberId(memberId)
                .map(MemberOrganizationTeam::getOrganization)
                .orElseThrow(() -> new CustomException(HomeErrorCode.MY_TEAM_NOT_FOUND));
    }


    public DailyRoutineResDto getDailyRoutine() {
        return getDailyRoutineByDate(LocalDate.now());
    }

    public DailyRoutineResDto getDailyRoutineByDate(LocalDate date) {
        Long memberId =AuthService.getCurrentUserId();

        WorkScheduleContext context = workScheduleService.getWorkScheduleContext(memberId, date);
        return dailyRoutineService.buildRoutine(context);
    }
    
    @Getter
    @AllArgsConstructor
    public enum HomeErrorCode implements ErrorReason {

        //근무 관련
        WORK_INSTANCE_NOT_FOUND("HOME001", HttpStatus.NOT_FOUND, "해당 일자에 저장된 근무 정보가 없습니다."),
        WORK_TIME_NOT_FOUND("HOME002",HttpStatus.NOT_FOUND, "오늘의 근무 시간 정보가 없습니다."),
        MY_TEAM_NOT_FOUND("HOME003", HttpStatus.NOT_FOUND, "해당하는 팀이 없습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}