package com.offnal.shifterz.jwt.exception;

import com.offnal.shifterz.global.exception.ErrorReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum JwtErrorCode implements ErrorReason {
    LOGOUT_TOKEN("JWT001", HttpStatus.UNAUTHORIZED, "이미 로그아웃된 토큰입니다."),
    EXPIRED_TOKEN("JWT002", HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    INVALID_TOKEN("JWT003", HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다.");

    private final String code;
    private final HttpStatus status;
    private final String message;
}
