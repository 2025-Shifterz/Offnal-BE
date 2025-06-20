package com.offnal.shifterz.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

    @GetMapping("/page")
    public ResponseEntity<Map<String, String>> loginPage() {
        String location = kakaoService.getKakaoAuthorizationUrl();

        // JSON 형태로 반환
        Map<String, String> response = new HashMap<>();
        response.put("location", location);
        return ResponseEntity.ok(response);
    }
}
