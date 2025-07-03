package com.offnal.shifterz.home.service;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.home.dto.HomeResDto;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final WorkInstanceRepository workInstanceRepository;
    private final MemberRepository memberRepository;

    //현재 근무 상태 받아오기
    public HomeResDto homeView(Long memberId) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.plusDays(1);

        WorkTimeType yesterdayType = findWorkTypeOrNull(yesterday, memberId);
        WorkTimeType todayType = findWorkTypeOrNull(today, memberId);
        WorkTimeType tomorrowType = findWorkTypeOrNull(tomorrow, memberId);

        return HomeResDto.from(yesterdayType, todayType, tomorrowType);
    }

    //근무 유형 가져오기
    private WorkTimeType findWorkTypeOrNull(LocalDate date, Long memberId) {
        String day = String.valueOf(date.getDayOfMonth());
        String year = String.valueOf(date.getYear());
        String month = String.valueOf(date.getMonthValue());

        return workInstanceRepository
                .findByWorkDayAndWorkCalendar_MemberIdAndWorkCalendar_YearAndWorkCalendar_Month(
                        day, memberId, year, month
                )
                .map(WorkInstance::getWorkTimeType)
                .orElse(null); // 근무 없으면 null
    }
    //ToDo 현재 근무 상태에 맞는 루틴 제공
}
