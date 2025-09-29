package com.offnal.shifterz.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class HealthGuideDto {
    @Schema(description = "공복 유지 관련 설명 문구")
    private String fastingComment;

    @Schema(description = "공복 유지가 필요한 시간대")
    private String fastingSchedule;

    @Schema(description = "추천 수면 시간 리스트")
    private List<String> sleepGuide;

    @Schema(description = "현재 시간 기준 가장 가까운 수면 일정")
    private String sleepSchedule;



    public static HealthGuideDto from(List<String> sleepGuide, String sleepSchedule, String fastingComment, String fastingSchedule) {
        return HealthGuideDto.builder()
                .fastingComment(fastingComment)
                .fastingSchedule(fastingSchedule)
                .sleepGuide(sleepGuide)
                .sleepSchedule(sleepSchedule)
                .build();
    }
}