package com.offnal.shifterz.home.dto;

import com.offnal.shifterz.work.domain.WorkTimeType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class WorkScheduleResponseDto {

    @Schema(description = "어제의 근무 유형")
    private WorkTimeType yesterdayType;

    @Schema(description = "오늘의 근무 유형")
    private WorkTimeType todayType;

    @Schema(description = "내일의 근무 유형")
    private WorkTimeType tomorrowType;

}