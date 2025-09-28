package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCalendarRequestDto {

    @NotEmpty(message = "근무표 이름은 필수입니다.")
    @Schema(description = "근무표 이름")
    private String calendarName;

    @NotNull
    @Schema(description = "조직 id")
    private Long organizationId;

    @Valid
    @NotEmpty(message = "근무 시간 정보는 필수입니다.")
    @Schema(description = "근무타입별 시간 정보(D/E/N)")
    private Map<String, WorkTimeDto> workTimes;

    @Valid
    @NotEmpty(message = "캘린더 목록은 필수입니다.")
    @Schema(description = "연도, 월, 날짜별 근무타입을 담은 캘린더 목록")
    private List<WorkCalendarUnitDto> calendars;
}
