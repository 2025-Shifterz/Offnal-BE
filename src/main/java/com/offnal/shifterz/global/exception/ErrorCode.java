package com.offnal.shifterz.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // 공통
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 인증 관련
    INVALID_KAKAO_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 카카오 액세스 토큰입니다."),
    KAKAO_USERINFO_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "카카오 사용자 정보 조회에 실패했습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

    // 회원 관련
    MEMBER_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "회원 등록에 실패했습니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),


    // 캘린더 저장 관련
    CALENDAR_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "근무표 이름은 필수입니다."),
    CALENDAR_YEAR_REQUIRED(HttpStatus.BAD_REQUEST, "연도는 필수입니다."),
    CALENDAR_MONTH_REQUIRED(HttpStatus.BAD_REQUEST, "월은 필수입니다."),
    CALENDAR_WORK_GROUP_REQUIRED(HttpStatus.BAD_REQUEST, "근무조는 필수입니다."),
    CALENDAR_WORK_TIME_REQUIRED(HttpStatus.BAD_REQUEST, "근무 시간 정보는 필수입니다."),
    CALENDAR_SHIFT_REQUIRED(HttpStatus.BAD_REQUEST, "근무일 정보는 필수입니다."),
    CALENDAR_DUPLICATION(HttpStatus.BAD_REQUEST, "이미 존재하는 연도/월의 캘린더입니다."),

    // 캘린더 수정 관련
    CALENDAR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 연도, 월의 캘린더를 찾을 수 없습니다."),

    // 캘린더 삭제 관련
    CALENDAR_DELETE_FAILED(HttpStatus.BAD_REQUEST, "근무표 삭제에 실패하였습니다."),

    //근무 관련
    WORK_INSTANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 일자에 저장된 근무 정보가 없습니다."),
    WORK_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "오늘의 근무 시간 정보가 없습니다."),

    // 근무일 조회 관련
    INVALID_YEAR_FORMAT(HttpStatus.BAD_REQUEST, "연도 형식이 올바르지 않습니다."),
    INVALID_MONTH_FORMAT(HttpStatus.BAD_REQUEST, "월 형식이 올바르지 않습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    // ErrorMessage -> ErrorCode
    public static ErrorCode fromMessage(String message) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.getMessage().equals(message)) {
                return errorCode;
            }
        }
        return INTERNAL_SERVER_ERROR;
    }
}