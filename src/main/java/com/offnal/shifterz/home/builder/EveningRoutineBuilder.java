package com.offnal.shifterz.home.builder;

import com.offnal.shifterz.global.util.TimeFormatter;
import com.offnal.shifterz.home.dto.*;
import com.offnal.shifterz.work.domain.WorkTime;

import java.time.LocalTime;
import java.util.List;

public class EveningRoutineBuilder implements RoutineBuilder {

    @Override
    public DailyRoutineResDto build(WorkScheduleContext context) {
        WorkTime workTime = context.getWorkTime();
        TimeRangeDto workRange = TimeRangeDto.from(workTime);

        LocalTime sleepStart = workRange.getEnd().plusHours(15);
        LocalTime fastingTime = workRange.getEnd().plusHours(1);

        return DailyRoutineResDto.from(
                List.of(
                        MealCardDto.from("아침", workRange.getStart().minusHours(7), "리듬 전환 대비", List.of("계란", "토스트")),
                        MealCardDto.from("점심", workRange.getStart().minusHours(2), "근무 전 에너지 확보", List.of("현미밥", "닭가슴살", "채소")),
                        MealCardDto.from("저녁", workRange.getEnd().minusHours(3), "과식 피하기", List.of("고구마", "두부 샐러드"))
                ),
                HealthGuideDto.from(
                        List.of("퇴근 후 바로 잠들면 내일 야간 근무에 지장이 갈 수 있어요"),
                        "밤샘 후 " + TimeFormatter.format(sleepStart) + " 수면",
                        "늦은 기상이므로 퇴근 후 과식 금지",
                        TimeFormatter.format(fastingTime) + " 이후 공복 유지"
                )
        );
    }
}

