package com.offnal.shifterz.kakao;

import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.jwt.JwtTokenProvider;
import com.offnal.shifterz.member.dto.AuthResponseDto;
import com.offnal.shifterz.member.service.MemberService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoLoginService {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public KakaoLoginResult loginWithKakao(String code) {
        // 1. 카카오 액세스 토큰 조회
        String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
        if (kakaoAccessToken == null || kakaoAccessToken.isBlank()) {
            throw new CustomException(ErrorCode.INVALID_KAKAO_TOKEN);
        }

        // 2. 사용자 정보 조회
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);
        if (userInfo == null || userInfo.getKakaoAccount() == null) {
            throw new CustomException(ErrorCode.KAKAO_USERINFO_FETCH_FAILED);
        }

        // 3. 회원 등록 or 업데이트
        MemberService.MemberResult result = memberService.registerOrUpdateKakaoMember(
                userInfo.getId(),
                userInfo.getKakaoAccount().getEmail(),
                userInfo.getKakaoAccount().getProfile().getNickName(),
                userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
        );
        if (result.getMember() == null) {
            throw new CustomException(ErrorCode.MEMBER_SAVE_FAILED);
        }

        // 4. 토큰 발급
        String jwtAccessToken = jwtTokenProvider.createToken(result.getMember().getEmail());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(result.getMember().getEmail());

        AuthResponseDto responseDto = AuthResponseDto.from(result.getMember(), result.isNewMember());

        return new KakaoLoginResult(responseDto, jwtAccessToken, jwtRefreshToken);
    }


}