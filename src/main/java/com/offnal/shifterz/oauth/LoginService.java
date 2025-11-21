package com.offnal.shifterz.oauth;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.jwt.JwtTokenProvider;
import com.offnal.shifterz.member.domain.Provider;
import com.offnal.shifterz.member.dto.AuthResponseDto;
import com.offnal.shifterz.member.dto.MemberResponseDto;
import com.offnal.shifterz.member.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final KakaoService kakaoService;
    private final AppleService appleService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthResponseDto loginWithSocial(Provider provider, String code) {

        if (provider == Provider.KAKAO) {
            String accessToken = kakaoService.getAccessToken(code);
            KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(accessToken);
            return handleKakaoLogin(userInfo);
        }

        else {
            throw new CustomException(LoginErrorCode.UNSUPPORTED_PROVIDER);
        }
    }

    public AuthResponseDto loginWithAppleNative(AppleLoginRequest request) {
        AppleUserInfoResponseDto userInfo = appleService.getUserInfoFromIdentityToken(request);
        return handleAppleLogin(userInfo, request);
    }

    private AuthResponseDto handleKakaoLogin(KakaoUserInfoResponseDto userInfo) {
        MemberResponseDto.MemberRegisterResponseDto result = memberService.registerMemberIfAbsent(
                Provider.KAKAO,
                String.valueOf(userInfo.getId()),
                userInfo.getKakaoAccount().getEmail(),
                userInfo.getKakaoAccount().getProfile().getNickName(),
                null,
                userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
        );

        return issueTokens(result);
    }
    private AuthResponseDto handleAppleLogin(AppleUserInfoResponseDto userInfo, AppleLoginRequest request) {
        // 닉네임은 클라이언트에서 전송한 fullName 사용 (최초 로그인 시에만 제공됨)
        String nickname = null;
        if (request.getFullName() != null) {
            nickname = request.getFullName().getFullName();
        }

        if (nickname == null || nickname.isBlank()) {
            nickname = "Apple User";
        }

        // 이메일도 클라이언트에서 전송한 값 우선 사용
        String email = request.getEmail() != null ? request.getEmail() : userInfo.getEmail();

        MemberResponseDto.MemberRegisterResponseDto result = memberService.registerMemberIfAbsent(
                Provider.APPLE,
                userInfo.getSub(),
                email,
                nickname,
                null,
                null
        );

        return issueTokens(result);
    }

    private AuthResponseDto issueTokens(MemberResponseDto.MemberRegisterResponseDto result) {
        if (result.getId() == null) {
            throw new CustomException(MemberService.MemberErrorCode.MEMBER_SAVE_FAILED);
        }

        String jwtAccessToken = jwtTokenProvider.createToken(result.getId());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(result.getId());

        return AuthResponseDto.from(result, jwtAccessToken, jwtRefreshToken);
    }

    @Getter
    @AllArgsConstructor
    private enum LoginErrorCode implements ErrorReason {
        UNSUPPORTED_PROVIDER("AUTH001", HttpStatus.BAD_REQUEST, "지원하지 않는 소셜 로그인 제공자입니다."),
        INVALID_SOCIAL_TOKEN("AUTH002", HttpStatus.UNAUTHORIZED, "유효하지 않은 소셜 액세스 토큰입니다."),
        SOCIAL_USERINFO_FETCH_FAILED("AUTH003", HttpStatus.BAD_REQUEST, "소셜 사용자 정보를 가져오지 못했습니다."),
        MEMBER_SAVE_FAILED("AUTH004", HttpStatus.INTERNAL_SERVER_ERROR, "회원 저장에 실패했습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}