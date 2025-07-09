package com.offnal.shifterz.work.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorkDayResponseDto {
    private String day;
    private String workTypeName;
}
