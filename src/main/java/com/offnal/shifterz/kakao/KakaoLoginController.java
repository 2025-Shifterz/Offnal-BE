package com.offnal.shifterz.kakao;

import com.offnal.shifterz.global.response.CustomApiResponse;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.member.dto.AuthResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 사용자 정보를 반환함",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AuthResponseDto.class),
                            examples = @ExampleObject(
                                    name = "성공 예시",
                                    value = "{\n" +
                                            "  \"code\": \"LOGIN_SUCCESS\",\n" +
                                            "  \"message\": \"로그인을 성공했습니다.\",\n" +
                                            "  \"data\": {\n" +
                                            "    \"nickname\": \"시프터즈\",\n" +
                                            "    \"newMember\": true,\n" +
                                            "    \"message\": \"로그인을 성공했습니다\"\n" +
                                            "  }\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
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
                    .body(CustomApiResponse.success(SuccessCode.LOGIN_SUCCESS, result.getAuthResponseDto()));


    }
}
