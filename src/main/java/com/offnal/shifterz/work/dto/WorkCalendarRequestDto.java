package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCalendarRequestDto {

    @NotEmpty(message = "근무표 이름은 필수입니다.")
    @NotNull
    @Schema(description = "근무표 이름")
    private String calendarName;

    @NotEmpty(message = "근무조는 필수입니다.")
    @NotNull
    @Schema(description = "근무조")
    private String workGroup;

    @Valid
    @NotEmpty(message = "근무 시간 정보는 필수입니다.")
    @NotNull
    @Schema(description = "근무타입별 시간 정보(D/E/N)")
    private Map<String, WorkTimeDto> workTimes;

    @Valid
    @NotEmpty(message = "캘린더 목록은 필수입니다.")
    @Schema(description = "연도, 월, 날짜별 근무타입을 담은 캘린더 목록")
    private List<WorkCalendarUnitDto> calendars;
}
