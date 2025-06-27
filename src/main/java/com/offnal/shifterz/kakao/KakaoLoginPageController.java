package com.offnal.shifterz.kakao;

import com.offnal.shifterz.global.exception.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "카카오 로그인", description = "카카오 소셜 로그인 페이지")
@RestController
@RequestMapping("/login")
public class KakaoLoginPageController {

    private final KakaoService kakaoService;

    public KakaoLoginPageController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @GetMapping("/page")
    @Operation(
            summary = "카카오 로그인 페이지 요청",
            description = "프론트에서 카카오 로그인 페이지로 리다이렉트하기 위한 URL을 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카카오 로그인 URL 반환",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = KakaoLoginPageResponse.class),
                            examples = @ExampleObject(
                                    name = "예시 응답",
                                    value = """
                                        {
                                          "location": "https://kauth.kakao.com/oauth/authorize?client_id=abc123&redirect_uri=http://localhost:8080/login/callback&response_type=code"
                                        }
                                        """
                            )
                    )
            ),
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
    public ResponseEntity<KakaoLoginPageResponse> loginPage() {
        return ResponseEntity.ok(kakaoService.getKakaoAuthorizationUrl());
    }
}
