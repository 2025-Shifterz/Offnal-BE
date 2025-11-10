package com.offnal.shifterz.home.service;

import com.offnal.shifterz.home.builder.RoutineBuilder;
import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.WorkScheduleContext;
import com.offnal.shifterz.home.factory.RoutineBuilderFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DailyRoutineService {
    private final RoutineBuilderFactory routineBuilderFactory;

    public DailyRoutineResDto buildRoutine(WorkScheduleContext context) {
        RoutineBuilder builder = routineBuilderFactory.getBuilder(context.getTodayType());
        return builder.build(context);
    }
}
