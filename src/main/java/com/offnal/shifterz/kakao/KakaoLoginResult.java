package com.offnal.shifterz.kakao;

import com.offnal.shifterz.member.dto.AuthResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class KakaoLoginResult {
    private final AuthResponseDto authResponseDto;
    private final String accessToken;
    private final String refreshToken;
}
