package com.offnal.shifterz.work.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkCalendarUnitDto;
import com.offnal.shifterz.work.dto.WorkCalendarUpdateDto;
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

import java.time.LocalDate;
import java.util.List;


@RestController
@RequestMapping("/works/calendar")
@RequiredArgsConstructor
@Tag(name = "근무 캘린더", description = "근무표를 저장, 조회, 수정, 삭제합니다.")
public class WorkCalendarController {

    private final WorkCalendarService workCalendarService;

    /**
     * 근무표 생성
     */
    @Operation(summary = "근무표 등록", description = "사용자의 근무표를 월별로 등록합니다.")
    @SuccessApiResponses.CreateCalendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.CreateWorkCalendar
    @PostMapping
    public SuccessResponse<Void> createWorkCalendar(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "근무표 등록 예시",
                            value = """
                            {
                              "calendarName": "병원 근무표",
                              "workTimes": {
                                "D": { "startTime": "08:00", "duration": "8:00" },
                                "E": { "startTime": "16:00", "duration": "8:00" },
                                "N": { "startTime": "00:00", "duration": "8:00" }
                              },
                              "calendars": [
                                {
                                  "startDate": "2025-09-01",
                                  "endDate": "2025-09-30",
                                  "shifts": {
                                    "2025-09-01": "E",
                                    "2025-09-02": "E",
                                    "2025-09-03": "N",
                                    "2025-09-04": "-"
                                  }
                                },
                                {
                                  "startDate": "2025-10-01",
                                  "endDate": "2025-10-30",
                                  "shifts": {
                                    "2025-10-01": "E",
                                    "2025-10-02": "E",
                                    "2025-10-03": "N",
                                    "2025-10-04": "-"
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
                    .workTimes(workCalendarRequestDto.getWorkTimes())
                    .calendars(List.of(unitDto))
                    .build();
            workCalendarService.saveWorkCalendar(requestDto);
        }
        return SuccessResponse.success(SuccessCode.CALENDAR_CREATED);
    }

    /**
     * 근무일 조회
     */
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
                                            { "date": "2025-09-01", "workTypeName": "오후" },
                                            { "date": "2025-09-02", "workTypeName": "오후" },
                                            { "date": "2025-09-03", "workTypeName": "야간" },
                                            { "date": "2025-09-04", "workTypeName": "휴무" }
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
    public SuccessResponse<List<WorkDayResponseDto>> getWorkDaysByStartDateAndEndDate(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ) {
        List<WorkDayResponseDto> response = workCalendarService.getWorkDaysByStartDateAndEndDate(startDate, endDate);
        return SuccessResponse.success(SuccessCode.DATA_FETCHED, response);

    }

    /**
     * 근무일 수정
     */
    @Operation(summary = "근무일 수정", description = "특정 연도와 월의 근무 일정을 수정합니다.")
    @SuccessApiResponses.UpdateCalendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.UpdateWorkCalendar
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무일 수정 성공",
                    content = @Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = WorkCalendarUpdateDto.class)),
                            examples = @ExampleObject(
                                    name = "근무일 수정 예시",
                                    value = """
                                        {
                                            "shifts": {
                                                "2025-09-01": "N",
                                                "2025-09-02": "D",
                                                "2025-09-03": "-",
                                                "2025-09-04": "E",
                                                "2025-09-05": "N"
                                            }
                                        }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping
    public SuccessResponse<Void> updateWorkCalendar(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "shifts": {
                                            "2025-09-01": "N",
                                            "2025-09-02": "D",
                                            "2025-09-03": "-",
                                            "2025-09-04": "E",
                                            "2025-09-05": "N"
                                        }
                                    }
                                    """))
            )
            @RequestBody @Valid WorkCalendarUpdateDto workCalendarUpdateDto){
        workCalendarService.updateWorkCalendar(startDate, endDate, workCalendarUpdateDto);
        return SuccessResponse.success(SuccessCode.CALENDAR_UPDATED);
    }


    /**
     * 근무표 삭제
     */
    @Operation(summary = "근무표 삭제", description = "특정 연도와 월의 근무표를 삭제합니다.")
    @SuccessApiResponses.DeleteCalendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.DeleteWorkCalendar
    @DeleteMapping
    public SuccessResponse<Void> deleteWorkCalendar(
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ){
        workCalendarService.deleteWorkCalendar(startDate, endDate);
        return SuccessResponse.success(SuccessCode.CALENDAR_DELETED);
    }

}
