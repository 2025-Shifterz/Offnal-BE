package com.offnal.shifterz.kakao;

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
        // 카카오 사용자 정보 조회
        String kakaoAccessToken = kakaoService.getKakaoAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        // 회원 등록 or 업데이트
        MemberService.MemberResult result = memberService.registerOrUpdateKakaoMember(
                userInfo.getId(),
                userInfo.getKakaoAccount().getEmail(),
                userInfo.getKakaoAccount().getProfile().getNickName(),
                userInfo.getKakaoAccount().getProfile().getProfileImageUrl()
        );

        // JWT 발급
        String jwtAccessToken = jwtTokenProvider.createToken(result.getMember().getEmail());
        String jwtRefreshToken = jwtTokenProvider.createRefreshToken(result.getMember().getEmail());

        AuthResponseDto responseDto = AuthResponseDto.from(result.getMember(), result.isNewMember());

        return new KakaoLoginResult(responseDto, jwtAccessToken, jwtRefreshToken);
    }


}