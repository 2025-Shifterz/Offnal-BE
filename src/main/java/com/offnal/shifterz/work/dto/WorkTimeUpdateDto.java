package com.offnal.shifterz.work.dto;

import com.offnal.shifterz.work.domain.WorkTimeType;
import jakarta.validation.Valid;
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
    @Valid
    @NotEmpty(message = "근무 시간 정보는 필수입니다.")
    private Map<WorkTimeType, WorkTimeDto> workTimes;
}
