package com.offnal.shifterz.oauth.apple;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.offnal.shifterz.global.config.AppleProperties;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.service.AppleSocialService;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppleService implements AppleSocialService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AppleProperties appleProperties;

    private static final String APPLE_PUBLIC_KEYS_URL = "https://appleid.apple.com/auth/keys";

    private Map<String, PublicKey> cachedKeys = new ConcurrentHashMap<>();
    private long lastFetchTime = 0L;


    @Override
    public AppleUserInfoResponseDto getUserInfoFromIdentityToken(AppleLoginRequest request) {

        String identityToken = request.getIdentityToken();

        DecodedJWT jwt = verifyIdentityToken(identityToken);

        String appleUserId = jwt.getSubject();
        String email = jwt.getClaim("email").asString();

        log.info("[Apple Login] UserID: {}, Email: {}", appleUserId, email);

        return new AppleUserInfoResponseDto(appleUserId, email);
    }


    @Override
    public DecodedJWT verifyIdentityToken(String identityToken) {

        try {
            DecodedJWT jwt = JWT.decode(identityToken);
            String kid = jwt.getKeyId();

            PublicKey publicKey = getApplePublicKey(kid);

            Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) publicKey, null);

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("https://appleid.apple.com")
                    .withAudience(appleProperties.clientId())
                    .build();

            return verifier.verify(identityToken);

        } catch (Exception e) {
            log.error("[Apple Login] Identity Token 검증 실패", e);
            throw new CustomException(AppleErrorCode.APPLE_TOKEN_INVALID);
        }
    }


    private PublicKey getApplePublicKey(String kid) {
        try {
            // 캐싱 만료 시간: 1시간
            if (cachedKeys.containsKey(kid) && System.currentTimeMillis() - lastFetchTime < 3600000) {
                return cachedKeys.get(kid);
            }

            String response = restTemplate.getForObject(APPLE_PUBLIC_KEYS_URL, String.class);
            JsonNode keys = objectMapper.readTree(response).get("keys");

            for (JsonNode key : keys) {
                String currentKid = key.get("kid").asText();

                String n = key.get("n").asText();
                String e = key.get("e").asText();

                PublicKey publicKey = createPublicKey(n, e);
                cachedKeys.put(currentKid, publicKey);
            }

            lastFetchTime = System.currentTimeMillis();

            if (!cachedKeys.containsKey(kid)) {
                throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_NOT_FOUND);
            }

            return cachedKeys.get(kid);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            log.error("[Apple Login] Public Key 가져오기 실패", e);
            throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_ERROR);
        }
    }


    private PublicKey createPublicKey(String n, String e) {
        try {
            byte[] nBytes = Base64.getUrlDecoder().decode(n);
            byte[] eBytes = Base64.getUrlDecoder().decode(e);

            BigInteger modulus = new BigInteger(1, nBytes);
            BigInteger exponent = new BigInteger(1, eBytes);

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");

            return factory.generatePublic(spec);

        } catch (Exception ex) {
            throw new CustomException(AppleErrorCode.APPLE_PUBLIC_KEY_ERROR);
        }
    }
    @Getter
    @AllArgsConstructor
    public enum AppleErrorCode implements ErrorReason {

        APPLE_TOKEN_INVALID("APL001", HttpStatus.UNAUTHORIZED, "유효하지 않은 Apple identity token입니다."),
        APPLE_PUBLIC_KEY_NOT_FOUND("APL002", HttpStatus.INTERNAL_SERVER_ERROR, "kid에 해당하는 Apple 공개키를 찾을 수 없습니다."),
        APPLE_PUBLIC_KEY_ERROR("APL003", HttpStatus.INTERNAL_SERVER_ERROR, "Apple 공개키 처리 중 오류가 발생했습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}