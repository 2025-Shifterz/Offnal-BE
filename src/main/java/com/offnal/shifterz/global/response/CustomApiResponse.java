package com.offnal.shifterz.global.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomApiResponse<T> {

    @Schema(description = "응답 코드", example = "LOGIN_SUCCESS")
    private final String code;

    @Schema(description = "응답 메시지", example = "로그인을 성공했습니다.")
    private final String message;

    @Schema(description = "응답 데이터")
    private final T data;

    public static <T> CustomApiResponse<T> success(SuccessCode code, T data) {
        return CustomApiResponse.<T>builder()
                .code(code.name())
                .message(code.getMessage())
                .data(data)
                .build();
    }

    public static CustomApiResponse<Void> success(SuccessCode code) {
        return CustomApiResponse.<Void>builder()
                .code(code.name())
                .message(code.getMessage())
                .data(null)
                .build();
    }
}