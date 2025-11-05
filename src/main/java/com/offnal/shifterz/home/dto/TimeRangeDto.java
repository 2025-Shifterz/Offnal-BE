package com.offnal.shifterz.home.dto;

import com.offnal.shifterz.work.domain.WorkTime;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TimeRangeDto {
    private final LocalTime start;
    private final LocalTime end;

    public static TimeRangeDto from(LocalTime start, LocalTime end) {
        return new TimeRangeDto(start, end);
    }

    public static TimeRangeDto from(WorkTime workTime) {
        LocalTime start = workTime.getStartTime();
        LocalTime end = start.plus(workTime.getDuration());
        return from(start, end);
    }
}

