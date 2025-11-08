package com.offnal.shifterz.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class MealCardDto {

    @Schema(description = "식사의 구분 (예: 아침, 점심, 저녁, 간식 등)")
    private String label;

    @Schema(description = "식사 시간 (HH:mm 형식)")
    private String time;

    @Schema(description = "식사 목적 또는 설명")
    private String description;

    @Schema(description = "권장 식사 메뉴 리스트")
    private List<String> items;

    public static MealCardDto from(String label, String time, String description, List<String> items) {
        return MealCardDto.builder()
                .label(label)
                .time(time)
                .description(description)
                .items(items)
                .build();
    }
}