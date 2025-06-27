package com.offnal.shifterz.global.exception;

import com.offnal.shifterz.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface ErrorApiResponses {

    //공토 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "INTERNAL_SERVER_ERROR", value = """
                                    {
                                      "code": "INTERNAL_SERVER_ERROR",
                                      "message": "서버 내부 오류가 발생했습니다."
                                    }
                                    """)
                    ))
    })
    @interface Common {}

   //인증 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "401", description = "유효하지 않은 카카오 토큰",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "INVALID_KAKAO_TOKEN", value = """
                                    {
                                      "code": "INVALID_KAKAO_TOKEN",
                                      "message": "유효하지 않은 카카오 액세스 토큰입니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "502", description = "카카오 사용자 정보 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "KAKAO_USERINFO_FETCH_FAILED", value = """
                                    {
                                      "code": "KAKAO_USERINFO_FETCH_FAILED",
                                      "message": "카카오 사용자 정보 조회에 실패했습니다."
                                    }
                                    """)
                    )),
            @ApiResponse(responseCode = "403", description = "접근 권한 없음",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "FORBIDDEN", value = """
                                    {
                                      "code": "FORBIDDEN",
                                      "message": "접근이 거부되었습니다."
                                    }
                                    """)
                    ))
    })
    @interface Auth {}

    //회원 관련 에러
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_NOT_FOUND", value = """
                                    {
                                      "code": "MEMBER_NOT_FOUND",
                                      "message": "존재하지 않는 회원입니다."
                                    }
                                    """)
                    ))
    })
    @interface Member {}
}
