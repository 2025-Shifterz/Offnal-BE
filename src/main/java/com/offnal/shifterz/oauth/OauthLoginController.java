package com.offnal.shifterz.oauth;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.exception.ErrorResponse;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.global.util.AuthResponseUtil;
import com.offnal.shifterz.member.domain.Provider;
import com.offnal.shifterz.member.dto.AuthResponseDto;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "소셜 로그인", description = "소셜 로그인 콜백 및 사용자 정보 반환 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/callback")
public class OauthLoginController {

    private final LoginService loginService;


    @Operation(summary = "카카오 로그인 콜백 처리", description = "카카오 인가 코드(code)를 기반으로 JWT 토큰과 사용자 정보를 반환합니다.")
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.Member
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = {
                                    @ExampleObject(name = "기존 회원 로그인", value = """
                                            {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"offnal",
                                                "newMember":false
                                                }
                                               }
                                            """),
                                    @ExampleObject(name = "신규 회원 로그인", value = """
                                             {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"offnal",
                                                "newMember":true
                                                }
                                               }
                                            """)
                            }),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Authorization",
                                    description = "Bearer {accessToken}",
                                    schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Refresh-Token",
                                    description = "Refresh Token",
                                    schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            )
                    }),
            @ApiResponse(responseCode = "500", description = "회원 등록 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_SAVE_FAILED", value = """
                                    {
                                      "code": "MEMBER_SAVE_FAILED",
                                      "message": "회원 등록에 실패했습니다."
                                    }
                                    """)
                    ))
    })
    @Hidden
    @GetMapping("")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        AuthResponseDto dto = loginService.loginWithSocial(Provider.KAKAO, code);
        return AuthResponseUtil.buildAuthResponse(dto);
    }

    @Operation(summary = "애플 로그인 콜백 처리", description = "애플 인가 코드(code)를 기반으로 JWT 토큰과 사용자 정보를 반환합니다.")
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @ErrorApiResponses.Member
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = {
                                    @ExampleObject(name = "기존 회원 로그인", value = """
                                            {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"Apple User",
                                                "newMember":false
                                                }
                                               }
                                            """),
                                    @ExampleObject(name = "신규 회원 로그인", value = """
                                             {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"Apple User",
                                                "newMember":true
                                                }
                                               }
                                            """)
                            }),
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Authorization",
                                    description = "Bearer {accessToken}",
                                    schema = @Schema(type = "string", example = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            ),
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Refresh-Token",
                                    description = "Refresh Token",
                                    schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
                            )
                    }),
            @ApiResponse(responseCode = "500", description = "회원 등록 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(name = "MEMBER_SAVE_FAILED", value = """
                                    {
                                      "code": "MEMBER_SAVE_FAILED",
                                      "message": "회원 등록에 실패했습니다."
                                    }
                                    """)
                    ))
    })
    @Hidden
    @PostMapping("/apple")
    public ResponseEntity<?> appleCallback(
            @RequestParam("code") String code,
            @RequestParam(value = "user", required = false) String user,
            @RequestParam(value = "state", required = false) String state
    ) {
        AuthResponseDto dto = loginService.loginWithSocial(Provider.APPLE, code);
        return AuthResponseUtil.buildAuthResponse(dto);
    }

}
