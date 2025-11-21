package com.offnal.shifterz.oauth;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.exception.ErrorApiResponses.AppleLoginError;
import com.offnal.shifterz.global.exception.ErrorResponse;
import com.offnal.shifterz.global.response.SuccessApiResponses.AppleLoginSuccess;
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
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("")
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
    @GetMapping("/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam("code") String code) {
        AuthResponseDto dto = loginService.loginWithSocial(Provider.KAKAO, code);
        return AuthResponseUtil.buildAuthResponse(dto);
    }
    @Operation(
            summary = "애플 로그인 (네이티브)",
            description = """
        iOS / React Native에서 받은 identityToken(JWT)을 검증하여 애플 로그인 또는 회원가입을 처리합니다.

        • identityToken을 Apple 공개키로 검증하고 사용자 식별자(sub)와 이메일을 파싱합니다.  
        • provider=APPLE, providerId=sub 조건으로 회원을 조회합니다.  
        • 기존 회원이면 바로 로그인 처리하고, 없으면 신규 회원을 자동 생성합니다.  
        • 애플 정책상 email과 이름은 최초 로그인에서만 제공될 수 있습니다.  
          이후 로그인에서는 전송되지 않아도 저장된 기존 정보로 정상적으로 로그인됩니다.

        클라이언트는 매 로그인마다 identityToken만 전송하면 됩니다.
        email/fullName은 최초 로그인 시 1회만 전달되어도 무방합니다.

        응답에는 회원 기본정보, 신규회원 여부(newMember), Access/Refresh Token이 포함됩니다.
        """
    )
    @AppleLoginSuccess
    @AppleLoginError
    @PostMapping("/login/apple")
    public SuccessResponse<AuthResponseDto> appleNativeLogin(
            @RequestBody AppleLoginRequest request
    ) {
        AuthResponseDto response = loginService.loginWithAppleNative(request);
        return SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, response);
    }


}
