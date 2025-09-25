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

    TODO_CREATED("TODO201", HttpStatus.CREATED, "할 일이 생성되었습니다."),
    TODO_UPDATED("TODO200", HttpStatus.OK, "할 일이 수정되었습니다."),
    TODO_FETCHED("TODO200", HttpStatus.OK, "할 일을 조회했습니다."),
    TODO_DELETED("TODO204", HttpStatus.NO_CONTENT, "할 일이 삭제되었습니다."),

    PROFILE_UPDATED("MEM001", HttpStatus.OK, "프로필 수정에 성공했습니다."),
    MY_INFO_FETCHED("MEM002", HttpStatus.OK, "내 정보 조회에 성공했습니다."),

    MEMO_CREATED("MEMO001", HttpStatus.CREATED, "메모가 성공적으로 생성되었습니다."),
    MEMO_UPDATED("MEMO002", HttpStatus.OK,  "메모가 성공적으로 수정되었습니다."),
    MEMO_FETCHED("MEMO003",HttpStatus.OK,  "메모가 성공적으로 조회되었습니다."),
    MEMO_DELETED("MEMO004", HttpStatus.OK, "메모가 성공적으로 삭제되었습니다."),

    ORGANIZATION_CREATED("ORG001", HttpStatus.CREATED, "조직이 성공적으로 생성되었습니다."),
    ORGANIZATION_UPDATED("ORG002", HttpStatus.OK,  "조직이 성공적으로 수정되었습니다."),
    ORGANIZATION_FETCHED("ORG003",HttpStatus.OK,  "조직이 성공적으로 조회되었습니다."),
    ORGANIZATION_DELETED("ORG004", HttpStatus.OK, "조직이 성공적으로 삭제되었습니다.");

    private final String code;
    private final HttpStatus httpStatus;
    private final String message;

    SuccessCode(String code, HttpStatus httpStatus, String message) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
