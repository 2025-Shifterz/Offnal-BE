package com.offnal.shifterz.oauth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.global.config.AppleProperties;
import com.offnal.shifterz.member.service.AppleSocialService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.InputStreamReader;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class AppleService implements AppleSocialService {

    private final AppleProperties appleProperties;

    @Override
    public TokenResponseDto getAppleToken(String code) {
        String clientSecret = createClientSecret();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", appleProperties.clientId());
        formData.add("client_secret", clientSecret);
        formData.add("code", code);
        formData.add("redirect_uri", appleProperties.redirectUri());

        return WebClient.create()
                .post()
                .uri("https://appleid.apple.com/auth/token")
                .header(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded")
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(TokenResponseDto.class)
                .block();
    }

    @Override
    public AppleUserInfoResponseDto getUserInfo(String idToken) {
        String[] parts = idToken.split("\\.");
        if (parts.length != 3) {
            throw new RuntimeException("Invalid Apple id_token");
        }

        String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map claims = mapper.readValue(payloadJson, Map.class);
            return mapper.convertValue(claims, AppleUserInfoResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Apple ID Token", e);
        }
    }


    @Override
    public AppleLoginPageResponse getAppleAuthorizationUrl() {

        String url = UriComponentsBuilder
                .fromHttpUrl("https://appleid.apple.com/auth/authorize")
                .queryParam("client_id", appleProperties.clientId())
                .queryParam("redirect_uri", appleProperties.redirectUri())
                .queryParam("response_type", "code id_token")
                .queryParam("response_mode", "form_post")
                .queryParam("scope", "name email")
                .encode()
                .toUriString();

        return new AppleLoginPageResponse(url);
    }


    /**
     * Apple Client Secret 생성
     */
    private String createClientSecret() {
        Date expiration = Date.from(
                LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()
        );

        return Jwts.builder()
                .setHeaderParam("kid", appleProperties.keyId())
                .setHeaderParam("alg", "ES256")
                .setIssuer(appleProperties.teamId())
                .setIssuedAt(new Date())
                .setExpiration(expiration)
                .setAudience("https://appleid.apple.com")
                .setSubject(appleProperties.clientId())
                .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                .compact();
    }

    private PrivateKey getPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource(appleProperties.privateKeyPath());
            PEMParser pemParser = new PEMParser(new InputStreamReader(resource.getInputStream()));
            PrivateKeyInfo privateKeyInfo = (PrivateKeyInfo) pemParser.readObject();
            pemParser.close();

            return new JcaPEMKeyConverter().getPrivateKey(privateKeyInfo);

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Apple private key", e);
        }
    }


}
