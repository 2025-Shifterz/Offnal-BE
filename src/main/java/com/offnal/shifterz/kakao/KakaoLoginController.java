package com.offnal.shifterz.kakao;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessResponse;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.member.dto.AuthResponseDto;
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
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "카카오 로그인", description = "카카오 로그인 콜백 및 사용자 정보 반환 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoLoginService kakaoLoginService;


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
                                                "nickname":"구혜승",
                                                "newMember":false
                                                }
                                               }
                                            """),
                                    @ExampleObject(name = "신규 회원 로그인", value = """
                                             {"code":"LOGIN_SUCCESS",
                                            "message":"로그인을 성공했습니다.",
                                            "data":{
                                                "nickname":"구혜승",
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
                    })
    })
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
            // 서비스 호출
             KakaoLoginResult result = kakaoLoginService.loginWithKakao(code);

            // 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + result.getAccessToken());
            headers.set("Refresh-Token", result.getRefreshToken());

            // 헤더 + 바디 응답
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, result.getAuthResponseDto()));


    }
}
