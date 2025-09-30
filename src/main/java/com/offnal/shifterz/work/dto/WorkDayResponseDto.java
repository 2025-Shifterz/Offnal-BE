package com.offnal.shifterz.work.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Data
@ToString
@Builder
public class WorkDayResponseDto {
    private LocalDate date;
    private String workTypeName;
}
