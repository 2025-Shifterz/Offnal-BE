package com.offnal.shifterz.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkCalendarMetaDto {
    private Long calendarId;
    private Map<String, WorkTimeDto> workTimes;
}
