package com.offnal.shifterz.work.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkCalendarUnitDto;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;
import com.offnal.shifterz.work.service.WorkCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/works/calendar")
@RequiredArgsConstructor
@Tag(name = "근무 캘린더 생성", description = "입력받은 근무표를 DB에 저장합니다.")
public class WorkCalendarController {

    private final WorkCalendarService workCalendarService;

    @Operation(summary = "근무표 등록")
    @SuccessApiResponses.Calendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.WorkCalendar
    @PostMapping
    public ResponseEntity<SuccessResponse<Void>> createWorkCalendar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "근무표 등록 예시",
                            value = """
                            {
                              "calendarName": "병원 근무표",
                              "workGroup": "1조",
                              "workTimes": {
                                "D": { "startTime": "08:00", "endTime": "16:00" },
                                "E": { "startTime": "16:00", "endTime": "00:00" },
                                "N": { "startTime": "00:00", "endTime": "08:00" }
                              },
                              "calendars": [
                                {
                                  "year": "2025",
                                  "month": "7",
                                  "shifts": {
                                    "1": "E",
                                    "2": "E",
                                    "3": "N",
                                    "4": "-"
                                  }
                                },
                                {
                                  "year": "2025",
                                  "month": "8",
                                  "shifts": {
                                    "1": "E",
                                    "2": "E",
                                    "3": "N",
                                    "4": "-"
                                  }
                                }
                              ]
                            }
                            """
                    )
            )
    )@RequestBody @Valid WorkCalendarRequestDto workCalendarRequestDto) {
        for(WorkCalendarUnitDto unitDto : workCalendarRequestDto.getCalendars()){
            WorkCalendarRequestDto requestDto = WorkCalendarRequestDto.builder()
                    .calendarName(workCalendarRequestDto.getCalendarName())
                    .workGroup(workCalendarRequestDto.getWorkGroup())
                    .workTimes(workCalendarRequestDto.getWorkTimes())
                    .calendars(List.of(unitDto))
                    .build();
            workCalendarService.saveWorkCalendar(requestDto);
        }
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.CALENDAR_CREATED));
    }

    @Operation(summary = "근무일 조회", description = "입력한 연도와 월에 해당하는 모든 날짜의 근무유형 정보를 반환합니다.")
    @SuccessApiResponses.WorkDay
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.WorkDay
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무일 조회 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WorkDayResponseDto.class)),
                            examples = @ExampleObject(
                                    name = "근무일 조회 예시",
                                    value = """
                                        [
                                          { "day": "1", "workTypeName": "오후" },
                                          { "day": "2", "workTypeName": "오후" },
                                          { "day": "3", "workTypeName": "야간" },
                                          { "day": "4", "workTypeName": "휴무" }
                                        ]
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })

    @GetMapping
    public ResponseEntity<SuccessResponse<List<WorkDayResponseDto>>> getWorkDaysByMonth(
            @RequestParam String year,
            @RequestParam String month
    ) {
        List<WorkDayResponseDto> response = workCalendarService.getWorkDaysByYearAndMonth(year, month);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.DATA_FETCHED, response));

    }

}
