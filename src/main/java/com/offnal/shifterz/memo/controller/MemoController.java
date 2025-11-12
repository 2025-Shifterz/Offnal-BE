package com.offnal.shifterz.memo.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.memo.dto.MemoRequestDto;
import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.memo.service.MemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "MEMO", description = "MEMO 관련 API")
@RestController
@RequestMapping("/memos")
@RequiredArgsConstructor
public class MemoController {

    private final MemoService memoService;

    /**
     * Memo 생성
     */
    @Operation(
            summary = "메모 생성",
            description = "새로운 메모를 생성합니다.\n\n" +
                    "✅ 요청 본문에 포함할 수 있는 값:\n" +
                    "- title: 메모 제목 (String, 필수)\n" +
                    "- content: 메모 내용 (String, 선택)\n" +
                    "- targetDate: 목표 날짜 (LocalDate, 기본 오늘 날짜)\n" +
                    "- organizationId: 소속 조직 ID (Long, 선택, **없으면 null로 보내세요**)"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "메모 생성 요청 예시",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "조직 포함",
                                    value = """
                                            {
                                              "title": "야간 근무 교대",
                                              "content": "일찍 도착하기",
                                              "targetDate": "2025-09-23",
                                              "organizationId": 10
                                            }
                                            """
                            ),
                            @ExampleObject(
                                    name = "조직 없음",
                                    value = """
                                            {
                                              "title": "스터디 준비",
                                              "content": "백엔드 공부",
                                              "targetDate": "2025-09-23",
                                              "organizationId": null
                                            }
                                            """
                            )
                    }
            )
    )
    @SuccessApiResponses.MemoCreate
    @ErrorApiResponses.Common
    @PostMapping
    public SuccessResponse<MemoResponseDto.MemoDto> createMemo(
            @RequestBody @Valid MemoRequestDto.CreateDto request
    ) {
        return SuccessResponse.success(SuccessCode.MEMO_CREATED, memoService.createMemo(request));
    }

    /**
     * Memo 수정
     */
    @Operation(summary = "메모 수정", description = "기존 메모를 수정합니다.")
    @SuccessApiResponses.MemoUpdate
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @PatchMapping
    public SuccessResponse<MemoResponseDto.MemoDto> updateMemo(
            @RequestBody @Valid MemoRequestDto.UpdateMemoDto request
    ) {
        return SuccessResponse.success(SuccessCode.MEMO_UPDATED, memoService.updateMemo(request));
    }

    /**
     * Memo 단건 조회
     */
    @Operation(summary = "메모 단건 조회", description = "특정 메모를 조회합니다.")
    @SuccessApiResponses.MemoGet
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping("/{id}")
    public SuccessResponse<MemoResponseDto.MemoDto> getMemo(@PathVariable Long id) {
        return SuccessResponse.success(SuccessCode.MEMO_FETCHED, memoService.getMemo(id));
    }

    @Operation(
            summary = "메모 목록 조회",
            description = """
                조건에 따라 내가 작성한 메모 목록을 조회합니다.  
                조합 가능한 파라미터들을 통해 다양한 조건으로 필터링할 수 있습니다.

                **조회 기준**
                - `filter` : 조회할 메모의 유형
                    - `all` → 내가 작성한 모든 메모 (기본값)
                    - `unassigned` → 소속 조직이 없는 메모만
                - `organizationId` : 특정 조직의 메모만 조회
                - `date` : 특정 날짜(`yyyy-MM-dd`)의 메모만 조회

                **조합 가능한 예시**
                | 조합 | 설명 |
                |------|------|
                | `filter=all` | 내가 작성한 전체 메모 |
                | `filter=unassigned` | 소속 없는 메모 |
                | `organizationId=3` | 조직 ID=3의 메모 |
                | `date=2025-11-09` | 2025년 11월 9일 작성된 메모 |
                | `filter=unassigned&date=2025-11-09` | 소속 없는 2025-11-09 메모 |
                | `organizationId=3&date=2025-11-09` | 조직 3의 2025-11-09 메모 |

                **주의사항**
                - `filter`, `organizationId`, `date`는 모두 **선택 파라미터**입니다.
                - 조합에 따라 동적으로 결과가 결정됩니다.
                - 파라미터를 생략하면 `filter=all` 기본값으로 전체 메모를 조회합니다.
                """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 메모 목록을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (필터 조합 오류 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 만료)", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @SuccessApiResponses.MemoGetAll
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping
    public SuccessResponse<List<MemoResponseDto.MemoDto>> getMemos(
            @Parameter(
                    name = "filter",
                    description = """
                        조회할 메모 유형입니다.
                        - `all` : 전체 메모 (기본값)
                        - `unassigned` : 소속 없는 메모
                        """,
                    example = "unassigned"
            )
            @RequestParam(required = false, defaultValue = "all") String filter,

            @Parameter(
                    name = "organizationId",
                    description = "특정 조직의 메모를 조회하려면 해당 조직의 ID를 입력하세요.",
                    example = "3"
            )
            @RequestParam(required = false) Long organizationId,

            @Parameter(
                    name = "date",
                    description = """
                        조회할 날짜를 입력하세요.  
                        `yyyy-MM-dd` 형식만 허용됩니다.  
                        (예: `2025-11-09`)
                        """,
                    example = "2025-11-09"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        List<MemoResponseDto.MemoDto> response = memoService.getMemos(filter, organizationId, date);
        return SuccessResponse.success(SuccessCode.MEMO_LIST_FETCHED, response);
    }


    /**
     * Memo 삭제
     */
    @Operation(summary = "메모 삭제", description = "특정 메모를 삭제합니다.")
    @SuccessApiResponses.MemoDelete
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @DeleteMapping("/{id}")
    public SuccessResponse<Void> deleteMemo(@PathVariable Long id) {
        memoService.deleteMemo(id);
        return SuccessResponse.success(SuccessCode.MEMO_DELETED, null);
    }
}
