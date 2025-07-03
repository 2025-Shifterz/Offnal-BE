package com.offnal.shifterz.work.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.service.WorkCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/work-calendar")
@RequiredArgsConstructor
@Tag(name = "근무 캘린더 생성", description = "입력받은 근무표를 DB에 저장합니다.")
public class WorkCalendarController {

    private final WorkCalendarService workCalendarService;

    @Operation(summary = "근무표 등록")
    @SuccessApiResponses.Calendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.WorkCalendar
    @ErrorApiResponses.WorkTime
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createWorkCalendar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "근무표 등록 예시",
                            value = """
                                        {
                                          "calendarName": "병원 근무표",
                                          "year": "2025",
                                          "month": "7",
                                          "workGroup": "1조",
                                          "workTimes": {
                                            "D": { "startTime": "08:00", "endTime": "16:00" },
                                            "E": { "startTime": "16:00", "endTime": "00:00" },
                                            "N": { "startTime": "00:00", "endTime": "08:00" }
                                          },
                                          "shifts": {
                                            "1": "E",
                                            "2": "E",
                                            "3": "N",
                                            "4": "-"
                                          }
                                        }
                                        """
                    )
            )
    )@RequestBody WorkCalendarRequestDto workCalendarRequestDto) {
        workCalendarService.saveWorkCalendar(workCalendarRequestDto);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.CALENDAR_CREATED));
    }
}
