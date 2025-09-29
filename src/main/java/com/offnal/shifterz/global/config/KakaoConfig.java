package com.offnal.shifterz.global.config;

import com.offnal.shifterz.kakao.KakaoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(KakaoProperties.class)
public class KakaoConfig {
}
