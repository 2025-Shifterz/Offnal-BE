package com.offnal.shifterz.todo.controller;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;
import com.offnal.shifterz.todo.service.TodoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Todo", description = "Todo 관련 API")

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
                    "- completed: 완료 여부 (Boolean, 기본 false)\n" +
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
                                              "completed": false,
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
                                              "completed": false,
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
    @PatchMapping
    public SuccessResponse<TodoResponseDto.TodoDto> updateTodo(
            @RequestBody @Valid TodoRequestDto.UpdateDto request
    ) {
        return SuccessResponse.success(SuccessCode.TODO_UPDATED, todoService.updateTodo(request));
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
     * Todo 목록 조회
     */
    @Operation(
            summary = "할일 목록 조회",
            description = """
                    조건에 따라 내가 작성한 투두(Todo) 목록을 조회합니다.
                    
                    **조회 기준**
                    - `filter` : 조회할 Todo 유형
                        - `all` → 내가 작성한 모든 Todo (기본값)
                        - `unassigned` → 소속 조직이 없는 Todo만
                    - `organizationId` : 특정 조직의 Todo만 조회
                    - `date` : 특정 날짜(`yyyy-MM-dd`)의 Todo만 조회
                    
                    **조합 가능한 예시**
                    | 조합 | 설명 |
                    |------|------|
                    | `filter=all` | 전체 Todo |
                    | `filter=unassigned` | 소속 없는 Todo |
                    | `organizationId=3` | 조직 ID=3의 Todo |
                    | `date=2025-11-09` | 2025년 11월 9일 작성된 Todo |
                    | `filter=unassigned&date=2025-11-09` | 소속 없는 2025-11-09 Todo |
                    | `organizationId=3&date=2025-11-09` | 조직 3의 2025-11-09 Todo |
                    
                    **주의사항**
                    - `filter`, `organizationId`, `date`는 모두 선택 파라미터입니다.
                    - 조합에 따라 동적으로 결과가 결정됩니다.
                    - 조직 id가 있다면 filter에는 파라미터를 기입하지 마세요.
                    - 파라미터를 생략하면 `filter=all`로 전체 Todo를 조회합니다.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 Todo 목록을 조회했습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (필터 조합 오류 등)", content = @Content),
            @ApiResponse(responseCode = "401", description = "인증 실패 (토큰 없음 또는 만료)", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    @SuccessApiResponses.TodoGetAll
    @GetMapping
    public SuccessResponse<List<TodoResponseDto.TodoDto>> getTodos(
            @Parameter(
                    name = "filter",
                    description = "조회할 Todo 유형 (`all`, `unassigned`)",
                    example = "unassigned",
                    schema = @Schema(allowableValues = {"all", "unassigned"})
            )
            @RequestParam(required = false, defaultValue = "all") String filter,

            @Parameter(
                    name = "organizationId",
                    description = "특정 조직의 Todo를 조회하려면 해당 조직의 ID를 입력하세요.",
                    example = "3"
            )
            @RequestParam(required = false) Long organizationId,

            @Parameter(
                    name = "date",
                    description = "조회할 날짜 (`yyyy-MM-dd` 형식, 예: `2025-11-09`)",
                    example = "2025-11-09"
            )
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        List<TodoResponseDto.TodoDto> response = todoService.getTodos(filter, organizationId, date);
        return SuccessResponse.success(SuccessCode.TODO_LIST_FETCHED, response);
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
