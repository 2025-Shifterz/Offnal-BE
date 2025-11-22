package com.offnal.shifterz.home.service;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.home.service.HomeService.HomeErrorCode;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.service.MemberService;
import com.offnal.shifterz.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.memberOrganizationTeam.repository.MemberOrganizationTeamRepository;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class WorkScheduleService {

    private final WorkInstanceRepository workInstanceRepository;
    private final MemberOrganizationTeamRepository memberOrganizationTeamRepository;

    public WorkTimeType findWorkType(LocalDate date, Long memberId, Organization organization) {
        return workInstanceRepository
                .findTypeByDateAndMemberAndOrganization(date, memberId, organization)
                .orElse(null);
    }

    public WorkScheduleContext getWorkScheduleContext(Long memberId, LocalDate date) {
        MemberOrganizationTeam mot = memberOrganizationTeamRepository
                .findByMemberId(memberId)
                .orElseThrow(() -> new CustomException(HomeErrorCode.MY_TEAM_NOT_FOUND));

        Organization organization = mot.getOrganization();

        LocalDate yesterday = date.minusDays(1);
        LocalDate tomorrow = date.plusDays(1);

        WorkTimeType yesterdayType = findWorkType(yesterday, memberId, organization);
        WorkTimeType todayType = findWorkType(date, memberId, organization);
        WorkTimeType tomorrowType = findWorkType(tomorrow, memberId, organization);

        // 오늘 근무 정보 조회
        WorkInstance todayWork = workInstanceRepository
                .findByWorkDateAndMemberIdAndOrganization(date, memberId, organization)
                .orElseThrow(() -> new CustomException(HomeErrorCode.WORK_INSTANCE_NOT_FOUND));

        WorkTime workTime = null;
        if (todayType != WorkTimeType.OFF) {
            String typeKey = todayType.getSymbol();
            workTime = todayWork.getWorkCalendar().getWorkTimes().get(typeKey);
            if (workTime == null) {
                throw new CustomException(HomeErrorCode.WORK_TIME_NOT_FOUND);
            }
        }

        return WorkScheduleContext.builder()
                .date(date)
                .yesterdayType(yesterdayType)
                .todayType(todayType)
                .tomorrowType(tomorrowType)
                .workTime(workTime)
                .build();
    }
}
