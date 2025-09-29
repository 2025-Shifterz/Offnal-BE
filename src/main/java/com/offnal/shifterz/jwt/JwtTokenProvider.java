package com.offnal.shifterz.jwt;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.member.repository.RefreshTokenRepository;
import com.offnal.shifterz.member.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private final MemberService memberService;
    private final MemberRepository memberRepository;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    private Key secretKey;

    @PostConstruct
    protected void init() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtProperties.secret());
        secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    // Access Token 생성
    public String createToken(Long memberId) {
        Claims claims = Jwts.claims().setSubject(memberId.toString()); // JWT payload 에 저장되는 정보단위
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + jwtProperties.accessTokenValiditySecond() * 1000))
                .signWith(secretKey)  // 암호화 알고리즘과, secret 값
                .compact();
    }

    // Refresh Token 생성
    public String createRefreshToken(Long memberId) {
        Claims claims = Jwts.claims().setSubject(memberId.toString());
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + jwtProperties.refreshTokenValiditySecond() * 1000))
                .signWith(secretKey)
                .compact();


        refreshTokenRepository.save(memberId, refreshToken, jwtProperties.refreshTokenValiditySecond(), TimeUnit.SECONDS);
        return refreshToken;
    }

    // 토큰에서 회원 정보 추출
    public Long getUserPk(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return Long.parseLong(claims.getSubject());
    }

    // 인증 정보 조회
    public Authentication getAuthentication(String token) {
        Long memberId = getUserPk(token);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        return new UsernamePasswordAuthenticationToken(
                customUserDetails,
                "",
                customUserDetails.getAuthorities()
        );
    }

    // 토큰 유효성, 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    // Request의 Header에서 token 값 가져오기
    public String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return request.getHeader("X-AUTH-TOKEN");
    }



}
