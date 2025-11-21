package com.offnal.shifterz.oauth;

import com.offnal.shifterz.global.config.AppleProperties;
import com.offnal.shifterz.member.service.SocialService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AppleService implements SocialService<AppleUserInfoResponseDto> {

    private final AppleProperties appleProperties;

    @Override
    public String getAccessToken(String code) {
        String clientSecret = createClientSecret();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", appleProperties.clientId());
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", appleProperties.redirectUri());

        TokenResponseDto appleTokenResponseDto = WebClient.create(appleProperties.url().token())
                .post()
                .uri("/auth/token")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new RuntimeException("Invalid Parameter")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new RuntimeException("Internal Server Error")))
                .bodyToMono(TokenResponseDto.class)
                .block();

        return appleTokenResponseDto.getAccessToken();
    }

    @Override
    public AppleUserInfoResponseDto getUserInfo(String accessToken) {
        // Apple ID Token(JWT)를 디코딩하여 사용자 정보 추출
        String[] tokenParts = accessToken.split("\\.");
        if (tokenParts.length != 3) {
            throw new RuntimeException("Invalid Apple ID Token format");
        }

        String payload = new String(Base64.getUrlDecoder().decode(tokenParts[1]));

        // Jackson ObjectMapper로 파싱
        try {
            com.fasterxml.jackson.databind.ObjectMapper objectMapper =
                    new com.fasterxml.jackson.databind.ObjectMapper();
            Map<String, Object> claims = objectMapper.readValue(payload, Map.class);

            AppleUserInfoResponseDto userInfo = new AppleUserInfoResponseDto();
            // Reflection을 사용하거나 builder 패턴으로 값 설정
            // 여기서는 간단히 표현
            return objectMapper.convertValue(claims, AppleUserInfoResponseDto.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Apple ID Token", e);
        }
    }

    /**
     * Apple Client Secret 생성 (JWT)
     * Apple은 매 요청마다 Client Secret을 동적으로 생성해야 함
     */
    private String createClientSecret() {
        Date expirationDate = Date.from(
                LocalDateTime.now().plusDays(30)
                        .atZone(ZoneId.systemDefault()).toInstant()
        );

        return Jwts.builder()
                .setHeaderParam("kid", appleProperties.keyId())
                .setHeaderParam("alg", "ES256")
                .setIssuer(appleProperties.teamId())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .setAudience("https://appleid.apple.com")
                .setSubject(appleProperties.clientId())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    /**
     * Private Key 파싱
     */
    private PrivateKey getPrivateKey() {
        try {
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(appleProperties.privateKeyPath())
            );

            PEMParser pemParser = new PEMParser(reader);
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            pemParser.close();

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            return converter.getPrivateKey(privateKeyInfo);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Apple private key", e);
        }
    }

    public AppleLoginPageResponse getAppleAuthorizationUrl() {
        String url = String.format(
                "https://appleid.apple.com/auth/authorize?response_type=code&client_id=%s&redirect_uri=%s&scope=name email&response_mode=form_post",
                appleProperties.clientId(),
                appleProperties.redirectUri()
        );
        return new AppleLoginPageResponse(url);
    }
}