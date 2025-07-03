package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkCalendarRequestDto {
    @Schema(description = "근무표 이름")
    private String calendarName;

    @Schema(description = "연도")
    private String year;

    @Schema(description = "월")
    private String month;

    @Schema(description = "근무조")
    private String workGroup;

    @Schema(description = "근무타입별 시간 정보(D/E/N)")
    private Map<String, WorkTimeDto> workTimes;

    @Schema(description = "근무표(날짜별 근무타입)")
    private Map<String, String> shifts;
}
