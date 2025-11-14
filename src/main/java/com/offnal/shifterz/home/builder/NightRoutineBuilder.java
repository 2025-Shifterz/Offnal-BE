package com.offnal.shifterz.home.builder;

import com.offnal.shifterz.global.util.TimeFormatter;
import com.offnal.shifterz.home.dto.*;
import com.offnal.shifterz.work.domain.WorkTime;

import java.time.LocalTime;
import java.util.List;

public class NightRoutineBuilder implements RoutineBuilder {

    @Override
    public DailyRoutineResDto build(WorkScheduleContext context) {
        WorkTime workTime = context.getWorkTime();
        TimeRangeDto workRange = TimeRangeDto.from(workTime);

        SleepScheduleBuilder sleepBuilder = new SleepScheduleBuilder()
                .addSchedule(workRange.getStart().minusHours(11), workRange.getStart().minusHours(6))
                .addSchedule(workRange.getEnd().plusHours(2), workRange.getEnd().plusHours(7));

        LocalTime caffeineLimit = workRange.getEnd().minusHours(3);

        return DailyRoutineResDto.from(
                List.of(
                        MealCardDto.from("점심", "12:00", "야근 전 주요 에너지 확보", List.of("현미밥", "생선구이", "채소")),
                        MealCardDto.from("출근 전 간식", workRange.getStart().minusHours(5), "포만감 및 졸림 방지", List.of("고구마", "삶은 달걀", "두유")),
                        MealCardDto.from("근무 중 간식 1", workRange.getStart().plusHours(3), "혈당 안정 및 집중력 유지", List.of("바나나", "견과류")),
                        MealCardDto.from("근무 중 간식 2", workRange.getStart().plusHours(6), "혈당 안정 및 집중력 유지", List.of("삶은 계란", "따뜻한 물")),
                        MealCardDto.from("퇴근 직후 소식", workRange.getEnd().plusMinutes(30), "위 부담 줄이며 안정된 수면 유도", List.of("연두부", "물"))
                ),
                HealthGuideDto.from(
                        sleepBuilder.getScheduleTexts(),
                        sleepBuilder.getClosestScheduleComment(),
                        "퇴근 후 원활한 수면을 위해 " + TimeFormatter.format(caffeineLimit) + " 이후엔 카페인 섭취 금지",
                        TimeFormatter.format(caffeineLimit) + " 이후 공복 유지"
                )
        );
    }
}

