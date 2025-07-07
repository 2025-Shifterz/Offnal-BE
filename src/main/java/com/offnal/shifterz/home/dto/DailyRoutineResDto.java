package com.offnal.shifterz.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DailyRoutineResDto {

    @Schema(description = "하루 동안의 식사 정보 리스트 (아침, 점심, 저녁 또는 간식 포함)")
    private List<MealCardDto> meals;

    @Schema(description = "수면 일정 및 공복 시간 등 건강 가이드 정보")
    private HealthGuideDto health;

    public static DailyRoutineResDto from(List<MealCardDto> meals, HealthGuideDto health) {
        return DailyRoutineResDto.builder()
                .meals(meals)
                .health(health)
                .build();
    }
}