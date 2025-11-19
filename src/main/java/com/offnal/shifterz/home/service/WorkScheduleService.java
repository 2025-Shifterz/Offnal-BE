package com.offnal.shifterz.home.service;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.home.service.HomeService.HomeErrorCode;
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

    public WorkTimeType findWorkType(LocalDate date, Long memberId) {
        return workInstanceRepository
                .findByWorkDateAndMemberIdThroughWorkCalendar(date, memberId)
                .map(WorkInstance::getWorkTimeType)
                .orElse(null);

    }

    public WorkScheduleContext getWorkScheduleContext(Long memberId, LocalDate date) {
        LocalDate yesterday = date.minusDays(1);
        LocalDate tomorrow = date.plusDays(1);

        WorkTimeType yesterdayType = findWorkType(yesterday, memberId);
        WorkTimeType todayType = findWorkType(date, memberId);
        WorkTimeType tomorrowType = findWorkType(tomorrow, memberId);

        // 오늘 근무 정보 조회
        WorkInstance todayWork = workInstanceRepository
                .findByWorkDateAndMemberIdThroughWorkCalendar(date, memberId)
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
