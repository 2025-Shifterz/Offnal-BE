package com.offnal.shifterz.global.exception;


import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorCode errorCode;
    private final ErrorReason errorReason;

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.errorReason = null;
    }

    public CustomException(ErrorReason errorReason) {
        super(errorReason.getMessage());
        this.errorReason = errorReason;
        this.errorCode = null;
    }
}
