package com.offnal.shifterz.home.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.home.converter.HomeDetailConverter;
import com.offnal.shifterz.home.converter.WorkScheduleConverter;
import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.home.dto.WorkScheduleResponseDto;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.work.domain.WorkTimeType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final WorkScheduleService workScheduleService;
    private final DailyRoutineService dailyRoutineService;
    private final WorkScheduleConverter workScheduleConverter;
    private final HomeDetailConverter homeDetailConverter;

    public WorkScheduleResponseDto getWorkSchedule() {
        Long memberId =AuthService.getCurrentUserId();

        LocalDate today = LocalDate.now();
        WorkTimeType todayType = workScheduleService.findWorkType(today, memberId);

        return workScheduleConverter.toDto(todayType);
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
        WORK_TIME_NOT_FOUND("HOME002",HttpStatus.NOT_FOUND, "오늘의 근무 시간 정보가 없습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}