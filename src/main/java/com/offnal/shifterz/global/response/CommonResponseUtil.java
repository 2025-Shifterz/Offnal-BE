package com.offnal.shifterz.global.response;

import org.springframework.http.ResponseEntity;

public class CommonResponseUtil {

    public static <T> ResponseEntity<CustomApiResponse<T>> ok(SuccessCode code, T data) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(CustomApiResponse.success(code, data));
    }

    public static ResponseEntity<CustomApiResponse<Void>> ok(SuccessCode code) {
        return ResponseEntity.status(code.getHttpStatus())
                .body(CustomApiResponse.success(code));
    }
}