package com.offnal.shifterz.work.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class WorkDayResponseDto {
    private LocalDate date;
    private String workTypeName;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    private Duration duration;
}
