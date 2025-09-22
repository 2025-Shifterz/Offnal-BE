package com.offnal.shifterz.todo.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;
import com.offnal.shifterz.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.offnal.shifterz.global.response.SuccessApiResponses;

@RestController
@RequestMapping("/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    /**
     * Todo 생성
     */
    @Operation(
            summary = "할 일 생성",
            description = "새로운 할 일을 생성합니다.\n\n" +
                    "✅ 요청 본문에 포함할 수 있는 값:\n" +
                    "- content: 할 일 내용 (String)\n" +
                    "- isSuccess: 완료 여부 (Boolean, 기본 false)\n" +
                    "- targetDate: 목표 날짜 (LocalDate, 기본 오늘 날짜)\n" +
                    "- organizationId: 소속 조직 ID (Long, 선택, **없으면 null로 보내세요**)"
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "할 일 생성 요청 예시",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    examples = {
                            @ExampleObject(
                                    name = "조직 포함",
                                    value = """
                {
                  "content": "스터디 준비",
                  "isSuccess": false,
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
                  "isSuccess": false,
                  "targetDate": "2025-09-23",
                  "organizationId": null
                }
                """
                            )
                    }
            )
    )

    @SuccessApiResponses.TodoCreate
    @ErrorApiResponses.Common
    @PostMapping
    public SuccessResponse<TodoResponseDto.TodoDto> createTodo(
            @RequestBody @Valid TodoRequestDto.CreateDto request
    ) {
        return SuccessResponse.success(SuccessCode.TODO_CREATED, todoService.createTodo(request));
    }

    /**
     * Todo 수정
     */
    @Operation(summary = "할 일 수정", description = "기존 할 일을 수정합니다.")
    @SuccessApiResponses.TodoUpdate
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @PatchMapping ("/{id}")
    public SuccessResponse<TodoResponseDto.TodoDto> updateTodo(
            @PathVariable Long id,
            @RequestBody @Valid TodoRequestDto.UpdateDto request
    ) {
        return SuccessResponse.success(SuccessCode.TODO_UPDATED, todoService.updateTodo(id, request));
    }

    /**
     * Todo 단건 조회
     */
    @Operation(summary = "할 일 단건 조회", description = "특정 할 일을 조회합니다.")
    @SuccessApiResponses.TodoGet
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @GetMapping("/{id}")
    public SuccessResponse<TodoResponseDto.TodoDto> getTodo(@PathVariable Long id) {
        return SuccessResponse.success(SuccessCode.TODO_FETCHED, todoService.getTodo(id));
    }

    /**
     * Todo 삭제
     */
    @Operation(summary = "할 일 삭제", description = "특정 할 일을 삭제합니다.")
    @SuccessApiResponses.TodoDelete
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @DeleteMapping("/{id}")
    public SuccessResponse<Void> deleteTodo(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return SuccessResponse.success(SuccessCode.TODO_DELETED, null);
    }
}
