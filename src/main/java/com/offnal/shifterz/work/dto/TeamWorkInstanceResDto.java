package com.offnal.shifterz.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeamWorkInstanceResDto {
    private String team;
    private List<WorkInstanceDto> workInstances;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class WorkInstanceDto {
        private LocalDate date;
        private String workType;
        private LocalTime startTime;
        private Duration duration;
    }
}
