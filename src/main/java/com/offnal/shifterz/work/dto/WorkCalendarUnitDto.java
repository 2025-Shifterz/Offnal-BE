package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Map;

@Getter
@Builder
public class WorkCalendarUnitDto {


    @NotNull(message = "시작일은 필수입니다.")
    @Schema(description = "시작일")
    private LocalDate startDate;


    @NotNull(message = "종료일은 필수입니다.")
    @Schema(description = "종료일")
    private LocalDate endDate;

    @Valid
    @NotNull(message = "근무일 정보는 필수입니다.")
    @Schema(description = "근무표(날짜별 근무타입)")
    private Map<LocalDate, String> shifts;
}
