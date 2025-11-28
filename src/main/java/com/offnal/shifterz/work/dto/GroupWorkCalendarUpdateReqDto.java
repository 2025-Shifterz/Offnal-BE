package com.offnal.shifterz.work.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class GroupWorkCalendarUpdateReqDto {
    @NotNull
    private List<GroupUnit> calendars;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class GroupUnit {

        @NotNull
        private String team;

        @NotNull
        private Map<LocalDate, String> shifts;
    }
}
