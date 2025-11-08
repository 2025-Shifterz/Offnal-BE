package com.offnal.shifterz.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCalendarListItemDto {
    private String calendarName;
    private LocalDate startDate;
    private LocalDate endDate;
}
