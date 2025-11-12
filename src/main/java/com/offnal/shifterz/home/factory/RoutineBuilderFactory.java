package com.offnal.shifterz.home.factory;

import com.offnal.shifterz.home.builder.*;
import com.offnal.shifterz.work.domain.WorkTimeType;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RoutineBuilderFactory {

    private final Map<WorkTimeType, RoutineBuilder> builders;

    public RoutineBuilderFactory() {
        this.builders = Map.of(
                WorkTimeType.OFF, new OffRoutineBuilder(),
                WorkTimeType.DAY, new DayRoutineBuilder(),
                WorkTimeType.EVENING, new EveningRoutineBuilder(),
                WorkTimeType.NIGHT, new NightRoutineBuilder()
        );
    }

    public RoutineBuilder getBuilder(WorkTimeType type) {
        return builders.get(type);
    }
}
