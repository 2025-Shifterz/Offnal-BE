package com.offnal.shifterz.work.dto;

import com.offnal.shifterz.work.domain.WorkTimeType;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkTimeUpdateDto {
    @NotEmpty
    private Map<WorkTimeType, WorkTimeDto> workTimes;
}
