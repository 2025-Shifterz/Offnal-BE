package com.offnal.shifterz.oauth;

import com.offnal.shifterz.member.domain.Provider;

public interface OAuthProvider {
    Provider getProviderType();

    OAuthUserInfoDto toOAuthUserInfoDto(Object rawUserInfoDto);
}
