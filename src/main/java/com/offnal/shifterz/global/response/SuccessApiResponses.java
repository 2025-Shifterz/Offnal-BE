package com.offnal.shifterz.global.response;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface SuccessApiResponses {

    // 공통 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "SUCCESS",
                                      "message": "요청이 정상적으로 처리되었습니다.",
                                      "data": null
                                    }
                                    """)
                    ))
    })
    public @interface Common {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무표 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "근무표 등록 성공 예시", value = """
                                    {
                                      "code": "CALENDAR_CREATED",
                                      "message": "근무표 등록에 성공했습니다.",
                                      "data": null
                                    }
                                """)
                    ))
    })
    public @interface CreateCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무표 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "근무표 수정 성공 예시", value = """
                                    {
                                      "code": "CALENDAR_UPDATED",
                                      "message": "근무표 수정에 성공했습니다.",
                                      "data": null
                                    }
                                    """)}
                    ))
    })
    public @interface UpdateCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무일 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "근무일 조회 성공 예시", value = """
                                    {
                                      "code": "WORK_DAY_FETCHED",
                                      "message": "근무일 조회에 성공했습니다.",
                                      "data": [
                                        { "day": "1", "workTypeName": "오후" },
                                        { "day": "2", "workTypeName": "오후" },
                                        { "day": "3", "workTypeName": "야간" },
                                        { "day": "4", "workTypeName": "휴무" }
                                      ]
                                    }
                                    """)
                    )
            )
    })
    public @interface WorkDay {}
}
