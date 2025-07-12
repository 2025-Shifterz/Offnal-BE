package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Builder
public class WorkCalendarUnitDto {

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

    @Valid
    @NotEmpty(message = "근무일 정보는 필수입니다.")
    @NotNull
    @Schema(description = "근무표(날짜별 근무타입)")
    private Map<String, String> shifts;
}
