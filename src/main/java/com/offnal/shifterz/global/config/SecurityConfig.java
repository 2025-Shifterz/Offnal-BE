package com.offnal.shifterz.global.config;

import com.offnal.shifterz.global.exception.CustomAuthenticationEntryPoint;
import com.offnal.shifterz.global.util.RedisUtil;
import com.offnal.shifterz.jwt.JwtAuthenticationFilter;
import com.offnal.shifterz.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisUtil redisUtil;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and() //CORS 활성화 추가
                .authorizeHttpRequests(authorizeRequests ->
                        authorizeRequests.requestMatchers(
                                        "/login/**",
                                        "/login/page/**",
                                        "/tokens/reissue",
                                        "/callback/**",
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/v3/api-docs/**",
                                        "/error").permitAll()
                                .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider, redisUtil, customAuthenticationEntryPoint),
                        UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        /*
         * 1) Apple callback은 Origin:null 요청
         * 모든 Origin 허용 필요
         */
        CorsConfiguration appleCors = new CorsConfiguration();
        appleCors.setAllowedOriginPatterns(List.of("*")); // Origin:null 허용
        appleCors.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
        appleCors.setAllowedHeaders(List.of("*"));
        appleCors.setAllowCredentials(true);

        source.registerCorsConfiguration("/callback/**", appleCors);


        /*
         * 일반 API는 Origin 제한
         */
        CorsConfiguration normalCors = new CorsConfiguration();
        normalCors.setAllowedOrigins(List.of("https://api.offnal.site", "http://localhost:8081"));
        normalCors.setAllowedMethods(List.of("GET", "POST", "PATCH", "DELETE", "OPTIONS"));
        normalCors.setAllowedHeaders(List.of("*"));
        normalCors.setAllowCredentials(true);

        source.registerCorsConfiguration("/**", normalCors);

        return source;
    }


}