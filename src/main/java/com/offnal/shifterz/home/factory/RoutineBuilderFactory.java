package com.offnal.shifterz.home.factory;

import com.offnal.shifterz.home.builder.DayRoutineBuilder;
import com.offnal.shifterz.home.builder.EveningRoutineBuilder;
import com.offnal.shifterz.home.builder.NightRoutineBuilder;
import com.offnal.shifterz.home.builder.OffRoutineBuilder;
import com.offnal.shifterz.home.builder.RoutineBuilder;
import com.offnal.shifterz.work.domain.WorkTimeType;
import java.util.Map;
import org.springframework.stereotype.Component;

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
