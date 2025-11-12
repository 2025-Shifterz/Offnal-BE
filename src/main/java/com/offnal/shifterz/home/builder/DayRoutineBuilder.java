package com.offnal.shifterz.home.builder;

import com.offnal.shifterz.global.util.TimeFormatter;
import com.offnal.shifterz.home.dto.*;
import com.offnal.shifterz.work.domain.WorkTime;

import java.time.LocalTime;
import java.util.List;

public class DayRoutineBuilder implements RoutineBuilder {

    @Override
    public DailyRoutineResDto build(WorkScheduleContext context) {
        WorkTime workTime = context.getWorkTime();
        TimeRangeDto workRange = TimeRangeDto.from(workTime);

        LocalTime sleepStart = workRange.getEnd().plusHours(6);
        LocalTime sleepEnd = workRange.getEnd().plusHours(13);
        LocalTime fastingTime = workRange.getEnd().plusHours(4);

        return DailyRoutineResDto.from(
                List.of(
                        MealCardDto.from("아침", workRange.getStart().minusHours(1), "기상 직후 에너지 공급", List.of("오트밀", "계란")),
                        MealCardDto.from("점심", workRange.getStart().plusHours(5), " 근무 집중력 유지", List.of("현미밥", "생선", "나물")),
                        MealCardDto.from("저녁", workRange.getEnd().plusHours(3), "소화 부담 없는 식사로 수면 대비", List.of("밥", "두부", "나물"))
                ),
                HealthGuideDto.from(
                        List.of("주간 근무 후, 오후 근무 대비해 늦게 수면"),
                        TimeFormatter.formatRange(sleepStart, sleepEnd) + " 수면",
                        "수면 질 향상 및 조기 기상 위해 저녁 일찍 → 공복 유지 후 수면",
                        TimeFormatter.format(fastingTime) + " 이후 공복 유지"
                )
        );
    }
}