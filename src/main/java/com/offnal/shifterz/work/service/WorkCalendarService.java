package com.offnal.shifterz.work.service;


import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class WorkCalendarService {

    private final WorkInstanceRepository workInstanceRepository;

    public List<WorkDayResponseDto> getWorkDaysByYearAndMonth(String year, String month) {
        List<WorkInstance> instances =
                workInstanceRepository.findByWorkCalendar_YearAndWorkCalendar_Month(year, month);

        return WorkCalendarConverter.toDayResponseDtoList(instances);
    }
}
