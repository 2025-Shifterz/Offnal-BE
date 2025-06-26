package com.offnal.shifterz.kakao;

import com.offnal.shifterz.jwt.JwtTokenProvider;
import com.offnal.shifterz.member.dto.AuthResponseDto;
import com.offnal.shifterz.member.service.MemberService;
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

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    @ApiResponse(responseCode = "200", description = "성공적으로 사용자 정보를 반환함",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = AuthResponseDto.class),
                examples = {
                        @ExampleObject(
                                name = "성공 예시",
                                value = "{\n" +
                                        "    \"memberId\": 1,\n" +
                                        "    \"email\": \"shifterz@naver.com\",\n" +
                                        "    \"nickname\": \"시프터즈\",\n" +
                                        "    \"profileImageUrl\": \"https://example.com/profile/example.jpg\",\n" +
                                        "    \"accessToken\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9\",\n" +
                                        "    \"refreshToken\": \"dGhpcyBpcyBhIHJlZnJlc2ggdG9rZW4uLi5tYXNrZWQ\",\n" +
                                        "    \"newMember\": true\n" +
                                        "}"
                        )
                }
    ))
    @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            // 1. 카카오 사용자 정보 조회
            String accessToken = kakaoService.getKakaoAccessToken(code);
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);

            // 2. 회원 등록 또는 업데이트
            MemberService.MemberResult result = memberService.registerOrUpdateKakaoMember(
                    userInfo.getId(),
                    userInfo.getKakaoAccount().getEmail(),
                    userInfo.getKakaoAccount().getProfile().getNickName(),
                    userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
            );

            // 3. JWT 토큰 발급
            String jwtAccessToken = jwtTokenProvider.createToken(result.getMember().getEmail());
            String jwtRefreshToken = jwtTokenProvider.createRefreshToken(result.getMember().getEmail());

            // 4. 헤더에 토큰 넣기
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtAccessToken);
            headers.set("Refresh-Token", jwtRefreshToken);

            // 5. 응답 DTO 생성
            AuthResponseDto response = AuthResponseDto.builder()
                    .nickname(result.getMember().getKakaoNickname())
                    .isNewMember(result.isNewMember())
                    .message("로그인을 성공했습니다")
                    .build();

            // 6. 응답 반환 (헤더 + 바디)
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(response);

        } catch (Exception e) {
            log.error("카카오 로그인 처리 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
