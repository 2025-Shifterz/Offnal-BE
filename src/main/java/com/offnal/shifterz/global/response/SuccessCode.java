package com.offnal.shifterz.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "로그인을 성공했습니다."),
    DATA_FETCHED(HttpStatus.OK, "데이터 조회에 성공했습니다."),
    CALENDAR_CREATED(HttpStatus.OK, "근무표 등록에 성공했습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}