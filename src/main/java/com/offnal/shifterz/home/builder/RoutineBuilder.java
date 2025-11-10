package com.offnal.shifterz.home.builder;

import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.WorkScheduleContext;

public interface RoutineBuilder {
    DailyRoutineResDto build(WorkScheduleContext context);
}
