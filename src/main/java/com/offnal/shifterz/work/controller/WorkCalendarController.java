package com.offnal.shifterz.work.controller;

import com.offnal.shifterz.work.converter.WorkCalendarConverter;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;
import com.offnal.shifterz.work.service.WorkCalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/works/calendar")
@RequiredArgsConstructor
public class WorkCalendarController {

    private final WorkCalendarService workCalendarService;

    @GetMapping
    public ResponseEntity<List<WorkDayResponseDto>> getWorkDaysByMonth(
            @RequestParam String year,
            @RequestParam String month
    ) {
        List<WorkDayResponseDto> response = workCalendarService.getWorkDaysByYearAndMonth(year, month);
        return ResponseEntity.ok(response);
    }

}
