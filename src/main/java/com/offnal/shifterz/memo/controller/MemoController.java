package com.offnal.shifterz.memo.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.memo.dto.MemoRequestDto;
import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.memo.service.MemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MEMO", description  = "MEMO 관련 API")
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
                    "- content: 메모 내용 (String)\n" +
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
                  "content": "야간 근무 교대",
                  "targetDate": "2025-09-23",
                  "organizationId": 10
                }
                """
                            ),
                            @ExampleObject(
                                    name = "조직 없음",
                                    value = """
                {
                  "content": "스터디 준비",
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
    /**
     * Memo 전체 조회
     */
    @Operation(
            summary = "메모 목록 조회",
            description = """
                조건에 따라 메모 목록을 조회합니다.

                ✅ 요청 파라미터:
                - filter=all : 내가 작성한 모든 메모
                - filter=unassigned : 소속 조직이 없는 메모만
                - organizationId : 특정 조직 ID에 속한 메모
                """
    )
    @SuccessApiResponses.MemoGetAll
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping
    public SuccessResponse<List<MemoResponseDto.MemoDto>> getMemos(
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) Long organizationId
    ) {
        List<MemoResponseDto.MemoDto> response = memoService.getMemos(filter, organizationId);
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
