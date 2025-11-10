package com.offnal.shifterz.home.builder;

import com.offnal.shifterz.global.util.TimeFormatter;
import com.offnal.shifterz.home.dto.SleepScheduleDto;
import java.util.Comparator;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class SleepScheduleBuilder {
    private final List<SleepScheduleDto> schedules = new ArrayList<>();

    public SleepScheduleBuilder addSchedule(LocalTime start, LocalTime end) {
        schedules.add(SleepScheduleDto.of(start, end, schedules.size() + 1));
        return this;
    }

    public List<String> getScheduleTexts() {
        return schedules.stream()
                .map(SleepScheduleDto::toText)
                .toList();
    }

    public String getClosestScheduleComment() {
        if (schedules.isEmpty()) {
            return "";
        }

        LocalTime now = LocalTime.now();
        SleepScheduleDto closest = schedules.stream()
                .min(Comparator.comparingLong(s -> s.minutesFrom(now)))
                .orElse(null);

        return TimeFormatter.formatRange(closest.getStart(), closest.getEnd()) + " 수면";
    }
}

