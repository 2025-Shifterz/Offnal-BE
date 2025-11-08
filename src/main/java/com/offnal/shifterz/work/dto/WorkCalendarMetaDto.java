package com.offnal.shifterz.work.dto;

import com.offnal.shifterz.work.domain.WorkTime;
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
    private String calendarName;
    private LocalDate startDate;
    private LocalDate endDate;
    private Map<String, WorkTimeDto> workTimes;
}
