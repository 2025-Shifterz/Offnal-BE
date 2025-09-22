package com.offnal.shifterz.global.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum SuccessCode {

    LOGIN_SUCCESS("AUTH001", HttpStatus.OK, "로그인을 성공했습니다."),
    DATA_FETCHED("COMMON001", HttpStatus.OK, "데이터 조회에 성공했습니다."),
    OK("COMMON002", HttpStatus.OK, "요청이 정상적으로 처리되었습니다."),
    CALENDAR_CREATED("CAL001", HttpStatus.OK, "근무표 등록에 성공했습니다."),
    CALENDAR_UPDATED("CAL002", HttpStatus.OK, "근무표 수정에 성공했습니다."),
    CALENDAR_DELETED("CAL003", HttpStatus.OK, "근무표 삭제에 성공했습니다."),


    PROFILE_UPDATED("MEM001", HttpStatus.OK, "프로필 수정에 성공했습니다."),
    MY_INFO_FETCHED("MEM002", HttpStatus.OK, "내 정보 조회에 성공했습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
