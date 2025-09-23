package com.offnal.shifterz.global.response;

import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public @interface SuccessApiResponses {

    // 공통 성공 응답
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "code": "SUCCESS",
                                      "message": "요청이 정상적으로 처리되었습니다.",
                                      "data": null
                                    }
                                    """)
                    ))
    })
    public @interface Common {
    }

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무표 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "근무표 등록 성공 예시", value = """
                                    {
                                      "code": "CALENDAR_CREATED",
                                      "message": "근무표 등록에 성공했습니다.",
                                      "data": null
                                    }
                                """)
                    ))
    })
    public @interface CreateCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무표 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "근무표 수정 성공 예시", value = """
                                    {
                                      "code": "CALENDAR_UPDATED",
                                      "message": "근무표 수정에 성공했습니다.",
                                      "data": null
                                    }
                                    """)}
                    ))
    })
    public @interface UpdateCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무표 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "근무표 삭제 성공 예시", value = """
                                    {
                                      "code": "CALENDAR_DELETED",
                                      "message": "근무표 삭제에 성공했습니다.",
                                      "data": null
                                    }
                                    """)}
                    ))
    })
    public @interface DeleteCalendar {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무일 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "근무일 조회 성공 예시", value = """
                                    {
                                      "code": "WORK_DAY_FETCHED",
                                      "message": "근무일 조회에 성공했습니다.",
                                      "data": [
                                           {
                                             "date": "2025-09-01",
                                             "workTypeName": "오후"
                                           },
                                           {
                                             "date": "2025-09-02",
                                             "workTypeName": "오후"
                                           },
                                           {
                                             "date": "2025-09-03",
                                             "workTypeName": "야간"
                                           },
                                           {
                                             "date": "2025-09-04",
                                             "workTypeName": "휴일"
                                           }
                                         ]
                                    }
                                    """)
                    )
            )
    })
    public @interface WorkDay {}
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "프로필 수정 성공 예시", value = """
                                {
                                  "code": "MEMBER_UPDATE_SUCCESS",
                                  "message": "프로필 수정에 성공했습니다.",
                                  "data": {
                                    "memberId": 1,
                                    "memberName": "홍길동",
                                    "email": "test@example.com",
                                    "phoneNumber": "010-1234-5678",
                                    "profileImageUrl": "https://cdn.com/profile.jpg"
                                  }
                                }
                                """)
                    )
            )
    })
    public @interface UpdateProfile {}
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "내 정보 조회 성공 예시", value = """
                                {
                                  "code": "MEM002",
                                  "message": "내 정보 조회에 성공했습니다.",
                                  "data": {
                                    "email": "example@kkukmoa.com",
                                    "name": "홍길동",
                                    "phoneNumber": "010-1234-5678",
                                    "profileImageUrl": "https://cdn.example.com/image.jpg"
                                  }
                                }
                                """)
                    )
            )
    })
    public @interface MyInfo {}
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "할 일 생성 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
     
                          "code": "TODO201",
                          "message": "할 일이 생성되었습니다.",
                          "result": {
                            "id": 1,
                            "content": "스터디 준비",
                            "isSuccess": false,
                            "targetDate": "2025-09-23",
                            "organizationId": 10,
                            "createdAt": "2025-09-22T10:12:45",
                            "updatedAt": "2025-09-22T10:12:45"
                          }
                        }
                        """)))
    })
    public @interface TodoCreate {}

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 수정 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
            
                          "code": "TODO200",
                          "message": "할 일이 수정되었습니다.",
                          "result": {
                            "id": 1,
                            "content": "스터디 준비 - 수정",
                            "isSuccess": true,
                            "targetDate": "2025-09-24",
                            "organizationId": 10,
                            "createdAt": "2025-09-22T10:12:45",
                            "updatedAt": "2025-09-22T11:30:00"
                          }
                        }
                        """)))
    })
    public @interface TodoUpdate {}

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "할 일 조회 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                 
                          "code": "TODO200",
                          "message": "할 일을 조회했습니다.",
                          "result": {
                            "id": 1,
                            "content": "스터디 준비",
                            "isSuccess": false,
                            "targetDate": "2025-09-23",
                            "organizationId": 10,
                            "createdAt": "2025-09-22T10:12:45",
                            "updatedAt": "2025-09-22T10:12:45"
                          }
                        }
                        """)))
    })
    public @interface TodoGet {}

    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "할 일 삭제 성공",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = """
                        {
                 
                          "code": "TODO204",
                          "message": "할 일이 삭제되었습니다.",
                          "result": null
                        }
                        """)))
    })
    public @interface TodoDelete {}

    @ApiResponse(
            responseCode = "201",
            description = "메모 생성 성공",
            content = @Content(
                    schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                    examples = @ExampleObject(
                            name = "메모 생성 성공 예시",
                            value = """
                {
       
                  "code": "MEMO001",
                  "message": "메모가 성공적으로 생성되었습니다.",
                  "result": {
                    "id": 1,
                    "content": "야간 근무 교대",
                    "targetDate": "2025-09-23",
                    "organizationId": 5
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoCreate {}


    @ApiResponse(
            responseCode = "200",
            description = "메모 수정 성공",
            content = @Content(
                    schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                    examples = @ExampleObject(
                            name = "메모 수정 성공 예시",
                            value = """
                {
                  "code": "MEMO002",
                  "message": "메모가 성공적으로 수정되었습니다.",
                  "result": {
                    "id": 1,
                    "content": "야간 근무 -> 주간 근무",
                    "targetDate": "2025-09-24",
                    "organizationId": 5
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoUpdate {}

    @ApiResponse(
            responseCode = "200",
            description = "메모 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                    examples = @ExampleObject(
                            name = "메모 조회 성공 예시",
                            value = """
                {

                  "code": "MEMO003",
                  "message": "메모가 성공적으로 조회되었습니다.",
                  "result": {
                    "id": 1,
                    "content": "야간 근무 교대",
                    "targetDate": "2025-09-23",
                    "organizationId": 5
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoGet {}


    @ApiResponse(
            responseCode = "200",
            description = "메모 삭제 성공",
            content = @Content(
                    schema = @Schema(implementation = Void.class),
                    examples = @ExampleObject(
                            name = "메모 삭제 성공 예시",
                            value = """
                {
                  "code": "MEMO004",
                  "message": "메모가 성공적으로 삭제되었습니다.",
                  "result": null
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoDelete {}

    @ApiResponse(
            responseCode = "201",
            description = "조직 생성 성공",
            content = @Content(
                    schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class),
                    examples = @ExampleObject(
                            name = "조직 생성 성공 예시",
                            value = """
                {
                  "code": "ORG001",
                  "message": "조직이 성공적으로 생성되었습니다.",
                  "result": {
                    "id": 1,
                    "organizationName": "옾날 병원",
                    "team": "1조"
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OrganizationCreate {}

    @ApiResponse(
            responseCode = "200",
            description = "조직 수정 성공",
            content = @Content(
                    schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class),
                    examples = @ExampleObject(
                            name = "조직 수정 성공 예시",
                            value = """
                {
                  "code": "ORG002",
                  "message": "조직이 성공적으로 수정되었습니다.",
                  "result": {
                    "id": 1,
                    "organizationName": "오프날 병원",
                    "team": "2조"
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OrganizationUpdate {}


    @ApiResponse(
            responseCode = "200",
            description = "조직 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class),
                    examples = @ExampleObject(
                            name = "조직 조회 성공 예시",
                            value = """
                {

                  "code": "ORG003",
                  "message": "조직이 성공적으로 조회되었습니다.",
                  "result": {
                    "id": 1,
                    "organizationName": "옾날 병원",
                    "team": "1조"
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OrganizationGet {}


    @ApiResponse(
            responseCode = "200",
            description = "조직 삭제 성공",
            content = @Content(
                    schema = @Schema(implementation = Void.class),
                    examples = @ExampleObject(
                            name = "조직 삭제 성공 예시",
                            value = """
                {
                  "code": "ORG004",
                  "message": "조직이 성공적으로 삭제되었습니다.",
                  "result": null
                }
                """
                    )
            )
    )
    @ApiResponse()
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OrganizationDelete {}
}
