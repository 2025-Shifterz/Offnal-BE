package com.offnal.shifterz.home.builder;

import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.HealthGuideDto;
import com.offnal.shifterz.home.dto.MealCardDto;
import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.work.domain.WorkTimeType;
import java.time.LocalTime;
import java.util.List;

public class OffRoutineBuilder implements RoutineBuilder {

    @Override
    public DailyRoutineResDto build(WorkScheduleContext context) {
        SleepScheduleBuilder sleepBuilder = new SleepScheduleBuilder();

        if (context.getYesterdayType() == WorkTimeType.NIGHT) {
            sleepBuilder.addSchedule(LocalTime.of(8, 0), LocalTime.of(13, 0));
        }
        if (context.getTomorrowType() == WorkTimeType.DAY) {
            sleepBuilder.addSchedule(LocalTime.of(22, 0), LocalTime.of(5, 0));
        }

        return DailyRoutineResDto.from(
                List.of(
                        MealCardDto.from("점심", "13:30", "기상 후 체력 회복", List.of("김밥", "칼국수")),
                        MealCardDto.from("저녁", "17:30", "밤잠 대비 소화 부담 최소화", List.of("죽", "나물", "연두부"))
                ),
                HealthGuideDto.from(
                        sleepBuilder.getScheduleTexts(),
                        sleepBuilder.getClosestScheduleComment(),
                        "생체 리듬 유지에 집중 야식, 피하고 수면 시간 지키기",
                        "저녁 식사 후 공복 유지"
                )
        );
    }
}

