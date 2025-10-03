package com.offnal.shifterz.work.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class WorkDayResponseDto {
    private LocalDate date;
    private String workTypeName;
}
