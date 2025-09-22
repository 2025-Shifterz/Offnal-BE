package com.offnal.shifterz.work.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class WorkDayResponseDto {
    private LocalDate date;
    private String workTypeName;
}
