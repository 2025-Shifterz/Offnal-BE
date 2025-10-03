package com.offnal.shifterz.jwt;

import com.offnal.shifterz.global.exception.ErrorApiResponses;
import com.offnal.shifterz.global.response.SuccessApiResponses;
import com.offnal.shifterz.global.response.SuccessCode;
import com.offnal.shifterz.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

    private final TokenService tokenService;

    @Operation(summary = "토큰 재발급", description = "만료된 Access Token을 재발급합니다. Refresh Token이 필요합니다.")
    @SuccessApiResponses.TokenReissue
    @ErrorApiResponses.Common
    @ErrorApiResponses.Auth
    @PostMapping("/reissue")
    public SuccessResponse<TokenDto.TokenResponse> reissue(@RequestBody TokenDto.TokenReissueRequest request) {
        TokenDto.TokenResponse response = tokenService.reissue(request);
        return SuccessResponse.success(SuccessCode.TOKEN_REISSUED, response);
    }

}
