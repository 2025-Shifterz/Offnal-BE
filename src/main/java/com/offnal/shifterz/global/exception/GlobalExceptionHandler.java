package com.offnal.shifterz.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("[CustomException] {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from(e.getErrorCode());
        return ResponseEntity.status(e.getErrorCode().getHttpStatus()).body(response);
    }

    // @Valid 유효성 검사 실패 시 에러코드 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.error("[ValidationException] {}", e.getMessage(), e);

        // 필드별 에러
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        String firstMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        ErrorCode errorCode = ErrorCode.fromMessage(firstMessage);

        ErrorResponse response = ErrorResponse.builder()
                .code(errorCode.name())
                .message(errorCode.getMessage())
                .errors(errors)
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[Exception] {}", e.getMessage(), e);
        ErrorResponse response = ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getHttpStatus()).body(response);
    }
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException e) {
        log.error("[BindException] {}", e.getMessage(), e);
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        ErrorResponse response = new ErrorResponse(ErrorCode.INVALID_REQUEST.name(), message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}