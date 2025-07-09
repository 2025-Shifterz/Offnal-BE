package com.offnal.shifterz.global.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "INTERNAL_SERVER_ERROR")
    private final String code;

    @Schema(description = "에러 메시지", example = "서버 내부 오류가 발생했습니다.")
    private final String message;

    @Schema(description = "필드별 에러 정보", example = "{\"calendarName\": \"근무표 이름은 필수입니다.\"}")
    private final Map<String, String> errors;

    public static ErrorResponse from(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .build();
    }

    public ErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
        this.errors = null;
    }
}