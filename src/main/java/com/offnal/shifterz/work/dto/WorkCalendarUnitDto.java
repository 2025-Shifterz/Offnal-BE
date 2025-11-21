package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
public class WorkCalendarUnitDto {

    @NotBlank(message = "조직 이름은 필수입니다.")
    @Schema(description = "조직 이름")
    private String organizationName;

    @NotBlank(message = "조 이름은 필수입니다.")
    @Schema(description = "조 이름")
    private String team;

    @Valid
    @NotNull(message = "근무일 정보는 필수입니다.")
    @Schema(description = "근무표(날짜별 근무타입)")
    private Map<LocalDate, String> shifts;
}
