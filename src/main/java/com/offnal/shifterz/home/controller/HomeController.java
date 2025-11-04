package com.offnal.shifterz.home.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.WorkScheduleResponseDto;
import com.offnal.shifterz.home.service.HomeService;
import com.offnal.shifterz.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Tag(name = "Home", description = "홈 관련 API")
public class HomeController {

    private final HomeService homeService;

    // 1. 근무 일정 조회 API (가벼운 API)
    @Operation(
            summary = "근무 일정 조회",
            description = "어제, 오늘, 내일의 근무 타입을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "근무 일정 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WorkScheduleResponseDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "data": {
                        "yesterday": "NIGHT",
                        "today": "OFF",
                        "tomorrow": "DAY"
                      }
                    }
                    """)
                    )
            )
    })
    @ErrorApiResponses.Common
    @GetMapping("/schedule")
    public ResponseEntity<SuccessResponse<WorkScheduleResponseDto>> getWorkSchedule(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMember().getId();
        WorkScheduleResponseDto responseDto = homeService.getWorkSchedule(memberId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, responseDto));
    }

    // 2. 오늘의 루틴 조회 API
    @Operation(
            summary = "오늘의 루틴 조회",
            description = "오늘의 식사, 수면, 공복 시간 등 건강 루틴 정보를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "루틴 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DailyRoutineResDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "data": {
                        "meals": [
                          {
                            "label": "점심",
                            "time": "13:30",
                            "description": "기상 후 체력 회복",
                            "items": ["김밥", "칼국수"]
                          },
                          {
                            "label": "저녁",
                            "time": "17:30",
                            "description": "밤잠 대비 소화 부담 최소화",
                            "items": ["죽", "나물", "연두부"]
                          }
                        ],
                        "health": {
                          "fastingComment": "생체 리듬 유지에 집중 야식, 피하고 수면 시간 지키기",
                          "fastingSchedule": "저녁 식사 후 공복 유지",
                          "sleepGuide": [
                            "(1) 08:00 ~ 13:00 수면",
                            "(2) 22:00 ~ 05:00 수면"
                          ],
                          "sleepSchedule": "수면 22:00 ~ 05:00"
                        }
                      }
                    }
                    """)
                    )
            )
    })
    @ErrorApiResponses.Common
    @GetMapping("/routine")
    public ResponseEntity<SuccessResponse<DailyRoutineResDto>> getDailyRoutine(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long memberId = userDetails.getMember().getId();
        DailyRoutineResDto responseDto = homeService.getDailyRoutine(memberId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, responseDto));
    }

    // 3. 특정 날짜의 루틴 조회 API (확장성)
    @Operation(
            summary = "특정 날짜 루틴 조회",
            description = "지정한 날짜의 식사, 수면, 공복 시간 등 건강 루틴 정보를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "루틴 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DailyRoutineResDto.class)
                    )
            )
    })
    @ErrorApiResponses.Common
    @GetMapping("/routine/{date}")
    public ResponseEntity<SuccessResponse<DailyRoutineResDto>> getDailyRoutineByDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Parameter(description = "조회할 날짜 (yyyy-MM-dd)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        Long memberId = userDetails.getMember().getId();
        DailyRoutineResDto responseDto = homeService.getDailyRoutineByDate(memberId, date);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, responseDto));
    }
}