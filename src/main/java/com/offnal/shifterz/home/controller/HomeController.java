package com.offnal.shifterz.home.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.home.dto.DailyRoutineResDto;
import com.offnal.shifterz.home.dto.HomeResDto;
import com.offnal.shifterz.home.service.HomeService;
import com.offnal.shifterz.jwt.CustomUserDetails;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTime;
import com.offnal.shifterz.work.domain.WorkTimeType;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Tag(name = "Home", description = "홈 관련 API")
public class HomeController {

    private final HomeService homeService;

    @ErrorApiResponses.Common
    @Operation(
            summary = "홈 화면 근무 상태 조회",
            description = """
                    홈 화면에 표시될 어제 / 오늘 / 내일의 근무 상태를 조회합니다.
                    
                    사용자의 근무표에 등록된 정보를 기준으로, 각 날짜의 근무 유형을 응답합니다.
                    """)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 근무 상태를 반환함",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HomeResDto.class),
                            examples = @ExampleObject(
                                    name = "근무 상태 예시",
                                    summary = "어제: EVENING / 오늘: NIGHT / 내일: OFF",
                                    value = """
                                            {
                                              "yesterday": "EVENING",
                                              "today": "NIGHT",
                                              "tomorrow": "OFF"
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "근무 정보가 존재하지 않음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "근무 정보 없음 예시",
                                    summary = "WORK_INSTANCE_NOT_FOUND",
                                    value = """
                                            {
                                              "code": "WORK_INSTANCE_NOT_FOUND",
                                              "message": "해당 일자에 저장된 근무 정보가 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<?> home(@AuthenticationPrincipal CustomUserDetails user) {
        HomeResDto resDto = homeService.homeView(user.getId());
        return ResponseEntity.ok(resDto);
    }

    // 오늘 근무 기준 루틴 제공
    @GetMapping("/routine")
    public DailyRoutineResDto getTodayRoutine(@AuthenticationPrincipal CustomUserDetails user) {
        return homeService.getTodayRoutine(user.getMember().getId());
    }
}
