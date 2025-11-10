package com.offnal.shifterz.home.dto;

import com.offnal.shifterz.work.domain.WorkTimeType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HomeDetailResponseDto {
    private WorkTimeType yesterdayType;
    private WorkTimeType todayType;
    private WorkTimeType tomorrowType;
    private DailyRoutineResDto todayRoutine;

    // 정적 팩토리 메서드 제거 (Converter로 이동)
}
