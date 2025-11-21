package com.offnal.shifterz.member.service;

import com.offnal.shifterz.oauth.AppleLoginPageResponse;
import com.offnal.shifterz.oauth.AppleUserInfoResponseDto;
import com.offnal.shifterz.oauth.TokenResponseDto;

public interface AppleSocialService {

    TokenResponseDto getAppleToken(String code);

    AppleUserInfoResponseDto getUserInfo(String idToken);

    AppleLoginPageResponse getAppleAuthorizationUrl();
}
