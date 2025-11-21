package com.offnal.shifterz.member.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.offnal.shifterz.oauth.apple.AppleLoginRequest;
import com.offnal.shifterz.oauth.apple.AppleUserInfoResponseDto;

public interface AppleSocialService {

    AppleUserInfoResponseDto getUserInfoFromIdentityToken(AppleLoginRequest request);

    DecodedJWT verifyIdentityToken(String identityToken);
}