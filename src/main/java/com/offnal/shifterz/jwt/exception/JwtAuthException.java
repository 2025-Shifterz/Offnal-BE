package com.offnal.shifterz.jwt.exception;

import com.offnal.shifterz.global.exception.ErrorReason;
import lombok.Getter;

import javax.naming.AuthenticationException;

@Getter
public class JwtAuthException extends RuntimeException {
    private final ErrorReason errorReason;

    public JwtAuthException(ErrorReason errorReason) {
        super(errorReason.getMessage());
        this.errorReason = errorReason;
    }
}
