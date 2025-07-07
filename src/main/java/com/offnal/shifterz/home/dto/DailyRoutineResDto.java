package com.offnal.shifterz.home.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyRoutineResDto {

    // 하루 식단 카드 리스트
    private List<MealCardDto> meals;

    // 공복 시간, 수면 시간 등 건강 가이드
    private HealthGuideDto health;
}