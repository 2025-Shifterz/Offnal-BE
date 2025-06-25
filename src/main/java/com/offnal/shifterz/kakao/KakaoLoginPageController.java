package com.offnal.shifterz.kakao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    public KakaoLoginPageController(KakaoService kakaoService) {
        this.kakaoService = kakaoService;
    }

    @Operation(summary = "카카오 로그인 페이지 URL 반환", description = "카카오 인증 페이지로 이동할 URL을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공적으로 URL 반환")
    @GetMapping("/page")
    public ResponseEntity<Map<String, String>> loginPage() {
        String location = kakaoService.getKakaoAuthorizationUrl();

        // JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("location", location);
        return ResponseEntity.ok(response);
    }
}
