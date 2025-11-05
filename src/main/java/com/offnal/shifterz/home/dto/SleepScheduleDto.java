package com.offnal.shifterz.home.dto;

import com.offnal.shifterz.global.util.TimeFormatter;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "of")
public class SleepScheduleDto {
    private final LocalTime start;
    private final LocalTime end;
    private final int index;

    public String toText() {
        return String.format("(%d) %s ~ %s 수면",
                index,
                TimeFormatter.format(start),
                TimeFormatter.format(end)
        );
    }

    public long minutesFrom(LocalTime time) {
        return Math.abs(start.toSecondOfDay() - time.toSecondOfDay()) / 60;
    }
}
