package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotEmpty(message = "근무표 이름은 필수입니다.")
    @NotNull
    @Schema(description = "근무표 이름")
    private String calendarName;

    @NotEmpty(message = "연도는 필수입니다.")
    @NotNull
    @Pattern(regexp = "\\d{4}", message = "연도 형식이 올바르지 않습니다.")
    @Schema(description = "연도")
    private String year;

    @NotEmpty(message = "월은 필수입니다.")
    @NotNull
    @Pattern(regexp = "^(0?[1-9]|1[0-2])$", message = "월 형식이 올바르지 않습니다.")
    @Schema(description = "월")
    private String month;

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
    @NotEmpty(message = "근무일 정보는 필수입니다.")
    @NotNull
    @Schema(description = "근무표(날짜별 근무타입)")
    private Map<String, String> shifts;
}
