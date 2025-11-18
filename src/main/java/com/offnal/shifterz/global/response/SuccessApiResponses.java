package com.offnal.shifterz.global.response;

import com.offnal.shifterz.jwt.TokenDto;
import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.organization.dto.OrganizationResponseDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
            @ApiResponse(responseCode = "200", description = "근무 시간 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "근무 시간 수정 성공 예시", value = """
                                    {
                                      "code": "WORK_TIME_UPDATED",
                                      "message": "근무 시간 수정에 성공했습니다.",
                                      "data": null
                                    }
                                    """)}
                    ))
    })
    public @interface UpdateWorkTime {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "근무 일정 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(name = "근무 일정 삭제 성공 예시", value = """
                                    {
                                      "code": "WORK_INSTANCES_DELETED",
                                      "message": "근무 일정 삭제에 성공했습니다.",
                                      "data": null
                                    }
                                    """)}
                    ))
    })
    public @interface DeleteInstance {}

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
                                            "date": "2025-07-01",
                                            "workTypeName": "오후",
                                            "startTime": "16:00",
                                            "duration": "PT6H30M"
                                          },
                                          {
                                            "date": "2025-07-02",
                                            "workTypeName": "오후",
                                            "startTime": "16:00",
                                            "duration": "PT6H30M"
                                          },
                                          {
                                            "date": "2025-07-03",
                                            "workTypeName": "야간",
                                            "startTime": "00:00",
                                            "duration": "PT6H30M"
                                          },
                                          {
                                            "date": "2025-07-04",
                                            "workTypeName": "휴일",
                                            "startTime": null,
                                            "duration": null
                                          },
                                          {
                                            "date": "2025-07-05",
                                            "workTypeName": "주간",
                                            "startTime": "08:00",
                                            "duration": "PT6H30M"
                                          },
                                          {
                                            "date": "2025-07-06",
                                            "workTypeName": "주간",
                                            "startTime": "08:00",
                                            "duration": "PT6H30M"
                                          },
                                          {
                                            "date": "2025-07-07",
                                            "workTypeName": "휴일",
                                            "startTime": null,
                                            "duration": null
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
            @ApiResponse(responseCode = "200", description = "캘린더 메타 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "캘린더 메타 정보 조회 성공 예시", value = """
                                       {
                                      "code": "CALENDAR_DATA_FETCHED",
                                      "message": "데이터 조회에 성공했습니다.",
                                      "data": {
                                        "calendarId": 1,
                                        "startDate": "2025-07-01",
                                        "endDate": "2025-07-07",
                                        "workTimes": {
                                          "D": {
                                            "startTime": "08:00",
                                            "duration": "PT6H30M"
                                          },
                                          "E": {
                                            "startTime": "16:00",
                                            "duration": "PT6H30M"
                                          },
                                          "N": {
                                            "startTime": "00:00",
                                            "duration": "PT6H30M"
                                          }
                                        }
                                      }
                                    }
                                    """)
                    )
            )
    })
    public @interface WorkCalendarMeta {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조직 내 전체 캘린더 메타 정보 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "조직 내 전체 캘린더 메타 정보 조회 예시", value = """
                                       {
                                                   "code": "CALENDAR_DATA_FETCHED",
                                                   "message": "캘린더 정보를 조회했습니다.",
                                                   "data": [
                                                     {
                                                       "calendarId": 1,
                                                       "startDate": "2025-08-01",
                                                       "endDate": "2025-08-07"
                                                     },
                                                     {
                                                       "calendarId": 2,
                                                       "startDate": "2025-07-01",
                                                       "endDate": "2025-07-07"
                                                     }
                                                   ]
                                                 }
                                    """)
                    )
            )
    })
    public @interface WorkCalendarList {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "organizationName이 같은 조직의 근무 일정 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "organizationName이 같은 조직의 근무 일정 조회 예시", value = """
                                    {
                                      "code": "CALENDAR_DATA_FETCHED",
                                      "message": "캘린더 정보를 조회했습니다.",
                                      "data": {
                                        "teams": [
                                          {
                                            "team": "1조",
                                            "workInstances": [
                                              {
                                                "date": "2025-07-01",
                                                "workType": "오후",
                                                "startTime": "16:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-02",
                                                "workType": "오후",
                                                "startTime": "16:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-03",
                                                "workType": "야간",
                                                "startTime": "00:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-04",
                                                "workType": "휴일",
                                                "startTime": null,
                                                "duration": null
                                              },
                                              {
                                                "date": "2025-07-05",
                                                "workType": "주간",
                                                "startTime": "08:00:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-06",
                                                "workType": "D",
                                                "startTime": "08:00:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-07",
                                                "workType": "휴일",
                                                "startTime": null,
                                                "duration": null
                                              }
                                            ]
                                          },
                                          {
                                            "team": "2조",
                                            "workInstances": [
                                              {
                                                "date": "2025-07-08",
                                                "workType": "주간",
                                                "startTime": "08:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-09",
                                                "workType": "주간",
                                                "startTime": "08:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-10",
                                                "workType": "오후",
                                                "startTime": "16:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-11",
                                                "workType": "오후",
                                                "startTime": "16:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-12",
                                                "workType": "야간",
                                                "startTime": "00:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-13",
                                                "workType": "야간",
                                                "startTime": "00:00",
                                                "duration": "PT6H30M"
                                              },
                                              {
                                                "date": "2025-07-14",
                                                "workType": "휴일",
                                                "startTime": null,
                                                "duration": null
                                              }
                                            ]
                                          }
                                        ]
                                      }
                                    }
                                    """)
                    )
            )
    })
    public @interface SameOrgWorkInstance {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "프로필 수정 성공 예시", value = """
                                    {
                                       "code": "PROFILE_UPDATED",
                                       "message": "프로필 수정에 성공했습니다.",
                                       "data": {
                                         "id": 3,
                                         "email": "test@offnal.com",
                                         "memberName": "테스트",
                                         "phoneNumber": "010-1111-1111",
                                         "profileImageKey": "profile/a360f82d-88e6-4e18-947b-804598d5570a.jpg",
                                         "profileImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/profile/member-1-profile"
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
                                       "code": "MY_INFO_FETCHED",
                                       "message": "내 정보 조회에 성공했습니다.",
                                       "data": {
                                         "id": 1,
                                         "email": "example@offnal.com",
                                         "memberName": "홍길동",
                                         "phoneNumber": null,
                                         "profileImageKey": "profile/a360f82d-88e6-4e18-947b-804598d5570a.jpg",
                                         "profileImageUrl": "https://bucket.s3.ap-northeast-2.amazonaws.com/profile/member-1-profile"
                                       }
                                     }
                                """)
                    )
            )
    })
    public @interface MyInfo {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "S3 업로드 url 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(name = "S3 업로드 url 생성 성공 예시", value = """
                                    {
                                          "code": "PROFILE_UPLOAD_URL_CREATED",
                                          "message": "S3용 프로필 사진 업로드 url 생성을 성공했습니다.",
                                          "data": {
                                            "uploadUrl": "https://offnal-s3-bucket.s3.ap-northeast-2.amazonaws.com/profile/member-3-profile?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Date=20251112T190907Z&X-Amz-SignedHeaders=content-type%3Bhost&X-Amz-Expires=300&X-Amz-Credential=AKIARGOKMTMG4PU4K2HM%2F20251112%2Fap-northeast-2%2Fs3%2Faws4_request&X-Amz-Signature=61ef9208f6d3e492e22dedee14aeed989ec52bfa76e30787746b0e3bcd62f58e",
                                            "key": "profile/a360f82d-88e6-4e18-947b-804598d5570a.jpg"
                                          }
                                        }
                                """)
                    )
            )
    })
    public @interface S3_URL {}

    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "할 일 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponseDto.TodoDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "할 일 생성 성공 예시",
                                            value = """
                    {
                      "code": "TODO201",
                      "message": "할 일이 생성되었습니다.",
                      "result": {
                        "id": 1,
                        "content": "스터디 준비",
                        "completed": false,
                        "targetDate": "2025-09-23",
                        "organizationId": 10
                      }
                    }
                    """
                                    )
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TodoCreate {}



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "할 일 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponseDto.TodoDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "할 일 수정 성공 예시",
                                            value = """
                    {
                      "code": "TODO200",
                      "message": "할 일이 수정되었습니다.",
                      "result": {
                        "id": 1,
                        "content": "스터디 준비 - 수정",
                        "completed": true,
                        "targetDate": "2025-09-24",
                        "organizationId": 10
                      }
                    }
                    """
                                    )
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TodoUpdate {}



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "할 일 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TodoResponseDto.TodoDto.class),
                            examples = {
                                    @ExampleObject(
                                            name = "할 일 조회 성공 예시",
                                            value = """
                    {
                      "code": "TODO200",
                      "message": "할 일을 조회했습니다.",
                      "result": {
                        "id": 1,
                        "content": "스터디 준비",
                        "completed": false,
                        "targetDate": "2025-09-23",
                        "organizationId": 10
           
                      }
                    }
                    """
                                    )
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TodoGet {}

    @ApiResponse(
            responseCode = "200",
            description = "할 일 목록 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = TodoResponseDto.TodoDto.class),
                    examples = @ExampleObject(
                            name = "할 일 목록 조회 성공 예시",
                            value = """
                {
                  "code": "TODO004",
                  "message": "할 일 목록을 성공적으로 조회했습니다.",
                  "result": [
                    {
                      "id": 1,
                      "content": "스터디 준비",
                      "completed": false,
                      "targetDate": "2025-09-23",
                      "organizationId": null
                    },
                    {
                      "id": 2,
                      "content": "야간 근무 교대",
                      "completed": true,
                      "targetDate": "2025-09-25",
                      "organizationId": 10
                    }
                  ]
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TodoGetAll {}


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "할 일 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class),
                            examples = {
                                    @ExampleObject(
                                            name = "할 일 삭제 성공 예시",
                                            value = """
                    {
                      "code": "TODO204",
                      "message": "할 일이 삭제되었습니다.",
                      "result": null
                    }
                    """
                                    )
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TodoDelete {}


    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "메모 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                            examples = {
                                    @ExampleObject(
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
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoCreate {}



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메모 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                            examples = {
                                    @ExampleObject(
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
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoUpdate {}



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메모 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                            examples = {
                                    @ExampleObject(
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
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoGet {}



    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "메모 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Void.class),
                            examples = {
                                    @ExampleObject(
                                            name = "메모 삭제 성공 예시",
                                            value = """
                    {
                      "code": "MEMO004",
                      "message": "메모가 성공적으로 삭제되었습니다.",
                      "result": null
                    }
                    """
                                    )
                            }
                    )
            )
    })
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoDelete {}
    @ApiResponse(
            responseCode = "200",
            description = "메모 전체 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = MemoResponseDto.MemoDto.class),
                    examples = @ExampleObject(
                            name = "메모 전체 조회 성공 예시",
                            value = """
                {
                  "code": "MEMO_LIST_FETCHED",
                  "message": "메모 목록을 성공적으로 조회했습니다.",
                  "result": [
                    {
                      "id": 1,
                      "content": "스터디 준비",
                      "targetDate": "2025-09-23",
                      "organizationId": null,
                      "createdAt": "2025-09-20T10:15:30",
                      "updatedAt": "2025-09-21T12:00:00"
                    },
                    {
                      "id": 2,
                      "content": "야간 근무 교대",
                      "targetDate": "2025-09-25",
                      "organizationId": 10,
                      "createdAt": "2025-09-22T09:00:00",
                      "updatedAt": "2025-09-23T14:20:00"
                    }
                  ]
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface MemoGetAll {}


    @ApiResponse(
            responseCode = "201",
            description = "조직 생성 성공",
            content = @Content(
                    schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class),
                    examples = @ExampleObject(
                            name = "조직 생성 성공 예시",
                            value = """
                                    {
                                      "code": "ORGANIZATION_CREATED",
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
                                      "code": "ORGANIZATION_UPDATED",
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
            description = "특정 조직 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class),
                    examples = @ExampleObject(
                            name = "특정 조직 조회 성공 예시",
                            value = """
                                    {
                                       "code": "ORGANIZATION_FETCHED",
                                       "message": "조직이 성공적으로 조회되었습니다.",
                                       "data": {
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
            description = "같은 이름의 조직 조회 성공",
            content = @Content(
                    schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class),
                    examples = @ExampleObject(
                            name = "같은 이름의 조직 조회 성공 예시",
                            value = """
                                    {
                                           "code": "ORGANIZATION_FETCHED",
                                           "message": "조직이 성공적으로 조회되었습니다.",
                                           "data": [
                                                {
                                                    "id": 1,
                                                    "organizationName": "병원 1",
                                                    "team": "1조"
                                                },
                                                {
                                                     "id": 2,
                                                     "organizationName": "병원 1",
                                                     "team": "2조"
                                                }
                                           ]
                                     }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface OrganizationTeamGet {}

    @ApiResponse(
            responseCode = "200",
            description = "전체 조직 조회 성공",
            content = @Content(
                    array = @ArraySchema(schema = @Schema(implementation = OrganizationResponseDto.OrganizationDto.class)),
                    examples = @ExampleObject(
                            name = "전체 조직 조회 성공 예시",
                            value = """
                                    {
                                      "code": "ORGANIZATION_FETCHED",
                                      "message": "조직이 성공적으로 조회되었습니다.",
                                      "data": [
                                        {
                                          "id": 1,
                                          "organizationName": "옾날 병원",
                                          "team": "1조"
                                        },
                                        {
                                          "id": 2,
                                          "organizationName": "오프날 병원",
                                          "team": "2조"
                                        }
                                      ]
                                    }
                                    """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AllOrganizationGet{}

    @ApiResponse(
            responseCode = "200",
            description = "조직 삭제 성공",
            content = @Content(
                    schema = @Schema(implementation = Void.class),
                    examples = @ExampleObject(
                            name = "조직 삭제 성공 예시",
                            value = """
                {
                  "code": "ORG005",
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

    @ApiResponse(
            responseCode = "200",
            description = "토큰 재발급 성공",
            content = @Content(
                    schema = @Schema(implementation = TokenDto.TokenResponse.class),
                    examples = @ExampleObject(
                            name = "토큰 재발급 성공 예시",
                            value = """
                {
                  "code": "TOKEN_REISSUED",
                  "message": "토큰이 재발급되었습니다.",
                  "data": {
                    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
                    "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
                  }
                }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface TokenReissue {}

    @ApiResponse(
            responseCode = "200",
            description = "로그아웃 성공",
            content = @Content(
                    examples = @ExampleObject(
                            name = "로그아웃 성공 예시",
                            value = """
                                    {
                                       "code": "LOGOUT_SUCCESS",
                                       "message": "로그아웃에 성공했습니다.",
                                       "data": null
                                     }
                """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Logout {}

    @ApiResponse(
            responseCode = "200",
            description = "회원 탈퇴 성공",
            content = @Content(
                    examples = @ExampleObject(
                            name = "회원 탈퇴 성공 예시",
                            value = """
                                    {
                                       "code": "MEMBER_DELETED",
                                       "message": "회원 탈퇴에 성공했습니다.",
                                       "data": null
                                     }
                                    """
                    )
            )
    )
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Withdraw {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "근무 일정 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "근무 일정 조회 성공 예시",
                                            value = """
                                            {
                                              "code": "HOME001",
                                              "message": "근무 일정을 성공적으로 조회했습니다.",
                                              "data": {
                                                "yesterdayType": "DAY",
                                                "todayType": "OFF",
                                                "tomorrowType": "DAY"
                                              }
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    public @interface HomeSchedule {}

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "오늘의 루틴 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "오늘의 루틴 조회 성공 예시",
                                            value = """
                                            {
                                              "code": "HOME002",
                                              "message": "루틴을 성공적으로 조회했습니다.",
                                              "data": {
                                                "meals": [
                                                  {
                                                    "label": "점심",
                                                    "time": "13:30",
                                                    "description": "기상 후 체력 회복",
                                                    "items": ["김밥", "칼국수"]
                                                  }
                                                ],
                                                "health": {
                                                  "fastingComment": "생체 리듬 유지에 집중",
                                                  "fastingSchedule": "저녁 식사 후 공복 유지",
                                                  "sleepGuide": ["08:00 ~ 13:00 수면"],
                                                  "sleepSchedule": "수면 22:00 ~ 05:00"
                                                }
                                              }
                                            }
                                            """
                                    )
                            }
                    )
            )
    })
    public @interface HomeRoutine {}


}
