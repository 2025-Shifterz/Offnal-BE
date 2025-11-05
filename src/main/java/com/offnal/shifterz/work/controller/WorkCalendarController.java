package com.offnal.shifterz.work.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.work.dto.WorkCalendarRequestDto;
import com.offnal.shifterz.work.dto.WorkCalendarUpdateDto;
import com.offnal.shifterz.work.dto.WorkDayResponseDto;
import com.offnal.shifterz.work.dto.WorkTimeUpdateDto;
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
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/works/calendar")
@RequiredArgsConstructor
@Tag(name = "근무 캘린더", description = "근무표를 저장, 조회, 수정, 삭제합니다.")
public class WorkCalendarController {

    private final WorkCalendarService workCalendarService;

    /**
     * 근무표 생성
     */
    @Operation(
            summary = "근무표 등록",
            description = "사용자의 근무표를 월별로 등록합니다.\n" +
                    "조직명이나 조 이름이 기존과 다르면, 새로운 조직으로 자동 생성됩니다.\n\n" +
                    "✅ 요청 본문에 포함할 수 있는 값:\n" +
                    "- calendarName: 근무표 이름\n" +
                    "- workTimes: 근무 시간 정보\n" +
                    "  - startTime: 근무 시작 시간\n" +
                    "  - duration: 근무 지속 시간 (HH:mm 형식)\n" +
                    "- calendars: 월별 근무 스케줄 목록\n" +
                    "  - organizationName: 조직 이름\n" +
                    "  - team: 조 이름\n" +
                    "  - startDate, endDate: 스케줄 기간 (YYYY-MM-DD)\n" +
                    "  - shifts: 날짜별 근무 타입 지정 (D=Day, E=Evening, N=Night, -=Off)"
    )
    @SuccessApiResponses.CreateCalendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.CreateWorkCalendar
    @PostMapping
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "근무표 등록 예시",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples =
                    @ExampleObject(
                            name = "근무표 등록 예시",
                            value = """
                                    {
                                      "calendarName": "2025년 7월 근무표",
                                      "workTimes": {
                                        "D": {
                                          "startTime": "08:00",
                                          "duration": "PT6H30M"
                                        },
                                        "E": {
                                          "startTime": "16:00",
                                          "duration": "PT6H30M"
                                        },
                                        "N": {
                                          "startTime": "00:00",
                                          "duration": "PT6H30M"
                                        }
                                      },
                                      "calendars": [
                                        {
                                          "organizationName": "병원 1",
                                          "team": "1조",
                                          "startDate": "2025-07-01",
                                          "endDate": "2025-07-07",
                                          "shifts": {
                                            "2025-07-01": "E",
                                            "2025-07-02": "E",
                                            "2025-07-03": "N",
                                            "2025-07-04": "-",
                                            "2025-07-05": "D",
                                            "2025-07-06": "D",
                                            "2025-07-07": "-"
                                          }
                                        },
                                        {
                                          "organizationName": "병원 1",
                                          "team": "2조",
                                          "startDate": "2025-07-08",
                                          "endDate": "2025-07-14",
                                          "shifts": {
                                            "2025-07-08": "D",
                                            "2025-07-09": "D",
                                            "2025-07-10": "E",
                                            "2025-07-11": "E",
                                            "2025-07-12": "N",
                                            "2025-07-13": "N",
                                            "2025-07-14": "-"
                                          }
                                        }
                                      ]
                                    }
                                    
                                    """
                    )
            )
    )
    public SuccessResponse<Void> createWorkCalendar(
            @RequestBody @Valid WorkCalendarRequestDto workCalendarRequestDto
    ) {
        workCalendarService.saveWorkCalendar(workCalendarRequestDto);
        return SuccessResponse.success(SuccessCode.CALENDAR_CREATED);
    }

    /**
     * 기간별 근무일 조회
     */

    @Operation(
            summary = "기간별 근무 조회",
            description = "startDate ~ endDate 사이의 근무일정을 조회합니다.\n" +
                    "✅ 요청 파라미터:\n" +
                    "- organizationName: 조직 이름 (필수)\n" +
                    "- team: 조 이름 (필수)\n" +
                    "- startDate, endDate: 스케줄 기간 (YYYY-MM-DD)\n\n"
    )
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
                                            {
                                              "code": "DATA_FETCHED",
                                              "message": "데이터 조회에 성공했습니다.",
                                              "data": [
                                                {
                                                  "date": "2025-09-01",
                                                  "workTypeName": "오후",
                                                  "startTime": "16:00",
                                                  "duration": "PT6H30M"
                                                },
                                                {
                                                  "date": "2025-09-02",
                                                  "workTypeName": "오후",
                                                  "startTime": "16:00",
                                                  "duration": "PT6H30M"
                                                },
                                                {
                                                  "date": "2025-09-03",
                                                  "workTypeName": "야간",
                                                  "startTime": "00:00",
                                                  "duration": "PT6H30M"
                                                },
                                                {
                                                  "date": "2025-09-04",
                                                  "workTypeName": "휴일",
                                                  "startTime": null,
                                                  "duration": null
                                                }
                                              ]
                                            }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public SuccessResponse<List<WorkDayResponseDto>> getWorkInstancesByRange(
            @RequestParam @NotNull String organizationName,
            @RequestParam @NotNull String team,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<WorkDayResponseDto> response = workCalendarService.getWorkInstancesByRange(
                organizationName, team, startDate, endDate);
        return SuccessResponse.success(SuccessCode.DATA_FETCHED, response);

    }

    /**
     * 월별 근무일 조회
     */

    @Operation(
            summary = "월별 근무 조회",
            description = "해당 연도와 월을 기반으로 조직의 근무일정을 조회합니다.\n\n" +
                    "✅ 요청 파라미터:\n" +
                    "- organizationName: 조직 이름 (필수)\n" +
                    "- team: 조 이름 (필수)\n" +
                    "- year: 조회할 연도\n" +
                    "- month: 조회할 월"
    )
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
                                            {
                                              "code": "DATA_FETCHED",
                                              "message": "데이터 조회에 성공했습니다.",
                                              "data": [
                                                {
                                                  "date": "2025-09-01",
                                                  "workTypeName": "오후",
                                                  "startTime": "16:00",
                                                  "duration": "PT6H30M"
                                                },
                                                {
                                                  "date": "2025-09-02",
                                                  "workTypeName": "오후",
                                                  "startTime": "16:00",
                                                  "duration": "PT6H30M"
                                                },
                                                {
                                                  "date": "2025-09-03",
                                                  "workTypeName": "야간",
                                                  "startTime": "00:00",
                                                  "duration": "PT6H30M"
                                                },
                                                {
                                                  "date": "2025-09-04",
                                                  "workTypeName": "휴일",
                                                  "startTime": null,
                                                  "duration": null
                                                }
                                              ]
                                            }
                                        """
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/monthly")
    public SuccessResponse<List<WorkDayResponseDto>> getMonthlyWorkInstances(
            @RequestParam @NotNull String organizationName,
            @RequestParam @NotNull String team,
            @RequestParam int year,
            @RequestParam int month
    ) {
        List<WorkDayResponseDto> response = workCalendarService.getMonthlyWorkInstances(
                organizationName, team, year, month);
        return SuccessResponse.success(SuccessCode.DATA_FETCHED, response);

    }

    /**
     * 근무일 수정
     */
    @Operation(summary = "근무일 수정", description = "특정 연도와 월의 근무 일정을 수정합니다. 해당 날짜에 기존의 근무 일정이 없을 경우, 근무 일정을 추가합니다.")
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
            @RequestParam @NotNull String organizationName,
            @RequestParam @NotNull String team,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                    {
                                        "shifts": {
                                            "2025-07-01": "N",
                                            "2025-07-02": "D",
                                            "2025-07-03": "-",
                                            "2025-07-04": "E"
                                        }
                                    }
                                    """))
            )
            @RequestBody @Valid WorkCalendarUpdateDto workCalendarUpdateDto){
        workCalendarService.updateWorkCalendar(organizationName, team, workCalendarUpdateDto);
        return SuccessResponse.success(SuccessCode.CALENDAR_UPDATED);
    }


    /**
     * 근무 일정 삭제
     */
    @Operation(summary = "근무 일정 삭제", description = "해당 기간의 근무 일정을 삭제합니다.")
    @SuccessApiResponses.DeleteCalendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.DeleteWorkCalendar
    @DeleteMapping("/instances")
    public SuccessResponse<Void> deleteWorkInstances(
            @RequestParam @NotNull String organizationName,
            @RequestParam @NotNull String team,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate
    ){
        workCalendarService.deleteWorkInstances(organizationName, team, startDate, endDate);
        return SuccessResponse.success(SuccessCode.WORK_INSTANCES_DELETED);
    }


    /**
     * 근무표 삭제
     */
    @Operation(summary = "근무표 삭제", description = "조직명, 팀, 근무표 이름에 해당하는 근무표와 하위 근무 일정 전체를 삭제합니다.")
    @SuccessApiResponses.DeleteCalendar
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.DeleteWorkCalendar
    @DeleteMapping
    public SuccessResponse<Void> deleteWorkCalendar(
            @RequestParam @NotNull String organizationName,
            @RequestParam @NotNull String team,
            @RequestParam @NotNull String calendarName
    ){
        workCalendarService.deleteWorkCalendar(organizationName, team, calendarName);
        return SuccessResponse.success(SuccessCode.CALENDAR_DELETED);
    }

    /**
     * 근무 시간 수정
     */
    @Operation(summary = "근무 시간 수정",
            description = "calendarName과 team으로 근무표를 찾아 workTimes(D/E/N/- 별 근무 시간)를 수정합니다. ")
    @SuccessApiResponses.UpdateWorkTime
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.CreateWorkCalendar
    @ErrorApiResponses.UpdateWorkCalendar
    @PatchMapping("/{organizationName}/{team}/{calendarName}/work-times")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "근무 시간 수정 예시",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples =
                    @ExampleObject(
                            name = "근무 시간 수정 예시",
                            value = """
                                    {
                                    "workTimes": {
                                        "DAY": {
                                          "startTime": "08:00",
                                          "duration": "PT6H"
                                        },
                                        "EVENING": {
                                          "startTime": "16:00",
                                          "duration": "PT6H"
                                        },
                                        "NIGHT": {
                                          "startTime": "00:00",
                                          "duration": "PT6H"
                                        }
                                      }
                                    }
                                    """
                    )
            )
    )
    public SuccessResponse<Void> updateWorkTimes(
            @PathVariable String organizationName,
            @PathVariable String team,
            @PathVariable String calendarName,
            @RequestBody @Valid WorkTimeUpdateDto request
    ){
        workCalendarService.updateWorkTimes(organizationName, team, calendarName, request);
        return SuccessResponse.success(SuccessCode.WORK_TIME_UPDATED);
    }
}
