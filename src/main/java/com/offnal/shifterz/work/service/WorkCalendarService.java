package com.offnal.shifterz.work.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkCalendarService {
    private final WorkCalendarRepository workCalendarRepository;
    private final WorkInstanceRepository workInstanceRepository;

    @Transactional
    public void saveWorkCalendar(WorkCalendarRequestDto workCalendarRequestDto) {

        Long memberId = AuthService.getCurrentUserId();

        WorkCalendar calendar = WorkCalendarConverter.toEntity(memberId, workCalendarRequestDto);
        WorkCalendar savedCalendar = workCalendarRepository.save(calendar);

        List<WorkInstance> instances = WorkCalendarConverter.toWorkInstances(workCalendarRequestDto, savedCalendar);
        workInstanceRepository.saveAll(instances);
    }
}
