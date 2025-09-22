package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class WorkCalendarUnitDto {

    @NotEmpty(message = "시작일은 필수입니다.")
    @NotNull
    @Schema(description = "시작일")
    private LocalDate startDate;

    @NotEmpty(message = "종료일은 필수입니다.")
    @NotNull
    @Schema(description = "종료일")
    private LocalDate endDate;

    @Valid
    @NotEmpty(message = "근무일 정보는 필수입니다.")
    @NotNull
    @Schema(description = "근무표(날짜별 근무타입)")
    private Map<LocalDate, String> shifts;
}
