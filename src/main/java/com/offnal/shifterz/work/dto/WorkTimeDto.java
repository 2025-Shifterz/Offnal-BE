package com.offnal.shifterz.work.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeDto {

    @NotEmpty(message = "근무 시간 정보는 필수입니다.")
    @NotNull
    @Schema(description = "근무 시작 시간", example = "08:00")
    private String startTime;

    @NotEmpty(message = "근무 시간 정보는 필수입니다.")
    @NotNull
    @Schema(description = "근무 시간", example = "3:00")
    private String duration;
}
