package com.offnal.shifterz.home.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.home.dto.HomeDetailResDto;
import com.offnal.shifterz.home.service.HomeService;
import com.offnal.shifterz.jwt.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Tag(name = "Home", description = "홈 관련 API")
public class HomeController {

    private final HomeService homeService;

    @Operation(
            summary = "홈 화면 정보 조회",
            description = "어제, 오늘, 내일의 근무 타입과 오늘의 루틴(식사, 수면, 공복 시간 등)을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "홈 데이터 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HomeDetailResDto.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "data": {
                        "yesterdayType": "NIGHT",
                        "todayType": "OFF",
                        "tomorrowType": "DAY",
                        "todayRoutine": {
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
                    }
                    """)
                    )
            ),
    })
    @ErrorApiResponses.Common
    @GetMapping
    public ResponseEntity<SuccessResponse<HomeDetailResDto>> getHomeDetail(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long memberId = userDetails.getMember().getId();
        HomeDetailResDto responseDto = homeService.getHomeDetail(memberId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK,responseDto));
    }
}