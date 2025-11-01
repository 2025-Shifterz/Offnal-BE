package com.offnal.shifterz.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

import java.time.Duration;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkTimeDto {

    @NotEmpty(message = "근무 시간 정보는 필수입니다.")
    @Schema(description = "근무 시작 시간", example = "08:00")
    @Pattern(regexp = "^\\d{1,2}:\\d{2}$", message = "HH:mm 형식이어야 합니다.")
    private String startTime;

    @NotNull(message = "근무 시간 정보는 필수입니다.")
    @Schema(description = "근무 시간 (ISO-8601)", example = "PT6H30M")
    private Duration duration;
}
