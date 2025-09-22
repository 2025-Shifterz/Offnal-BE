package com.offnal.shifterz.member.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.jwt.CustomUserDetails;
import com.offnal.shifterz.member.converter.MemberConverter;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.domain.Provider;
import com.offnal.shifterz.member.dto.MemberRequestDto;
import com.offnal.shifterz.member.dto.MemberResponseDto;
import com.offnal.shifterz.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 소셜 로그인 회원 등록 또는 업데이트
     * @param provider 소셜 제공자 (KAKAO, GOOGLE, NAVER ...)
     * @param providerId 제공자 ID
     * @param email 이메일
     * @param memberName 이름
     * @param phoneNumber 전화번호
     * @param profileImageUrl 프로필 이미지
     * @return 등록/업데이트된 Member와 신규 가입 여부
     */
    @Transactional
    public MemberResponseDto.MemberRegisterResponseDto registerOrUpdateMember(
            Provider provider,
            String providerId,
            String email,
            String memberName,
            String phoneNumber,
            String profileImageUrl
    ) {
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(provider, providerId);

        if (existingMember.isPresent()) {
            // 기존 회원 정보 업데이트
            Member member = existingMember.get();
            member.updateMemberInfo(email, memberName, phoneNumber, profileImageUrl);

            return MemberConverter.toRegisterResponse(member, false);
        } else {
            // 신규 회원 등록
            Member newMember = Member.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .memberName(memberName)
                    .phoneNumber(phoneNumber)
                    .profileImageUrl(profileImageUrl)
                    .build();

            Member savedMember = memberRepository.save(newMember);



            return MemberConverter.toRegisterResponse(savedMember, true);
        }
    }


    @Transactional
    public MemberResponseDto.MemberUpdateResponseDto updateProfile(MemberRequestDto.MemberUpdateRequestDto request) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateMemberInfo(
                request.getEmail(),
                request.getName(),
                request.getPhoneNumber(),
                request.getProfileImageUrl()

        );

        return MemberConverter.toResponse(member);
    }

    @Transactional
    public MemberResponseDto.MemberUpdateResponseDto getMyInfo() {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        return MemberConverter.toResponse(member);
    }
    @Getter
    @AllArgsConstructor
    public enum MemberErrorCode implements ErrorReason {
        MEMBER_NOT_FOUND("MEM001", HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
        MEMBER_SAVE_FAILED("MEM002", HttpStatus.INTERNAL_SERVER_ERROR, "회원 저장에 실패했습니다."),
        MEMBER_ACCESS_DENIED("MEM003", HttpStatus.FORBIDDEN, "회원 접근 권한이 없습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }

}
