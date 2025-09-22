package com.offnal.shifterz.member.service;

public interface SocialService<T> { // T = 소셜별 DTO
    String getAccessToken(String code);
    T getUserInfo(String accessToken);
}

