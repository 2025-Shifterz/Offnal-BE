package com.offnal.shifterz.jwt;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.global.util.RedisUtil;
import com.offnal.shifterz.member.repository.RefreshTokenRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RedisUtil redisUtil;


    /**
     * RT을 검증 후, 새 AT, RT 발급
     */
    public TokenDto.TokenResponse reissue(TokenDto.TokenReissueRequest request) {

        String oldRefreshToken = request.getRefreshToken();


        Long memberId = jwtTokenProvider.getUserPk(oldRefreshToken);


        // Redis에 저장된 RefreshToken과 비교
        String storedToken = refreshTokenRepository.findByMemberId(memberId);


        if (storedToken == null || !storedToken.equals(oldRefreshToken)) {

            throw new CustomException(TokenService.TokenErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createToken(memberId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);


        // Redis 값 갱신
        refreshTokenRepository.save(memberId, newRefreshToken, 14, TimeUnit.DAYS);

        return new TokenDto.TokenResponse(newAccessToken, newRefreshToken);
    }

    /**
     * 로그아웃 - 사용자의 Refresh Token 삭제
     */
    public void logout(String accessToken) {
        Long memberId = AuthService.getCurrentUserId();
        refreshTokenRepository.delete(memberId);

        long expiration = jwtTokenProvider.getExpiration(accessToken);
        if (expiration > 0) {
            redisUtil.setBlackList(accessToken, expiration, TimeUnit.MILLISECONDS);
        } else {
            throw new CustomException(TokenService.TokenErrorCode.INVALID_TOKEN);
        }
    }

    @Getter
    @AllArgsConstructor
    public enum TokenErrorCode implements ErrorReason {
        INVALID_TOKEN("AUTH001", HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
        UNAUTHORIZED("AUTH002", HttpStatus.FORBIDDEN, "로그인되지 않았습니다."),
        INVALID_REFRESH_TOKEN("AUTH003", HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다."),
        LOGOUT_TOKEN("AUTH004", HttpStatus.UNAUTHORIZED, "이미 로그아웃된 토큰입니다.");
        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}