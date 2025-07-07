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
public class HealthGuideDto {
    private String fastingComment; // 공복 시간 설명
    private String fastingSchedule;           // 공복 시간 안내
    private List<String> sleepGuide;   // 수면 시간 가이드
    private String sleepSchedule;

}