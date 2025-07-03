package com.offnal.shifterz.home.dto;

import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTimeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeResDto {

    private WorkTimeType yesterday; // 어제 근무 형태
    private WorkTimeType today;     // 오늘 근무 형태
    private WorkTimeType tomorrow;  // 내일 근무 형태

    public static HomeResDto from(WorkTimeType yesterday, WorkTimeType today, WorkTimeType tomorrow) {
        return HomeResDto.builder()
                .yesterday(yesterday)
                .today(today)
                .tomorrow(tomorrow)
                .build();
    }
}