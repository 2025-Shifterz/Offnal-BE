package com.offnal.shifterz.home.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MealCardDto {
    private String label;        // 예: 점심, 출근 전 간식
    private String time;         // 예: 12:00
    private String description;  // 예: 야근 전 에너지 확보
    private List<String> items; // 예: [현미밥, 생선구이, 채소]

    private MealCardDto meal(String label, String time, String desc, List<String> items) {
        return MealCardDto.builder()
                .label(label)
                .time(time)
                .description(desc)
                .items(items)
                .build();
    }
}
