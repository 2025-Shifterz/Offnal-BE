package com.offnal.shifterz.jwt;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
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


    /**
     * RT을 검증 후, 새 AT, RT 발급
     */
    public TokenDto.TokenResponse reissue(TokenDto.TokenReissueRequest request) {
        log.info(">>> [reissue] 요청 DTO = {}", request);

        String oldRefreshToken = request.getRefreshToken();
        log.info(">>> [reissue] 요청에서 꺼낸 oldRefreshToken = {}", oldRefreshToken);

        Long memberId = jwtTokenProvider.getUserPk(oldRefreshToken);
        log.info(">>> [reissue] 추출된 memberId = {}", memberId);

        // Redis에 저장된 RefreshToken과 비교
        String storedToken = refreshTokenRepository.findByMemberId(memberId);
        log.info(">>> [reissue] Redis에 저장된 storedToken = {}", storedToken);

        if (storedToken == null || !storedToken.equals(oldRefreshToken)) {
            log.warn(">>> [reissue] RefreshToken 불일치 or 없음 → 요청토큰={}, 저장토큰={}", oldRefreshToken, storedToken);
            throw new CustomException(TokenService.TokenErrorCode.INVALID_REFRESH_TOKEN);
        }

        String newAccessToken = jwtTokenProvider.createToken(memberId);
        String newRefreshToken = jwtTokenProvider.createRefreshToken(memberId);

        log.info(">>> [reissue] 새 AccessToken = {}", newAccessToken);
        log.info(">>> [reissue] 새 RefreshToken = {}", newRefreshToken);

        // Redis 값 갱신
        refreshTokenRepository.save(memberId, newRefreshToken, 14, TimeUnit.DAYS);
        log.info(">>> [reissue] Redis에 새로운 RefreshToken 저장 완료 (memberId={})", memberId);

        return new TokenDto.TokenResponse(newAccessToken, newRefreshToken);
    }

    @Getter
    @AllArgsConstructor
    private enum TokenErrorCode implements ErrorReason {
        INVALID_REFRESH_TOKEN("AUTH003", HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh Token입니다.");
        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}