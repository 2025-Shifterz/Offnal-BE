package com.offnal.shifterz.member.service;

import com.offnal.shifterz.jwt.CustomUserDetails;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.domain.Provider;
import com.offnal.shifterz.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public MemberResult registerOrUpdateMember(
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

            return new MemberResult(member, false);
        } else {
            // 신규 회원 등록 (Builder 활용)
            Member newMember = Member.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .memberName(memberName)
                    .phoneNumber(phoneNumber)
                    .profileImageUrl(profileImageUrl)
                    .build();

            Member savedMember = memberRepository.save(newMember);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    new CustomUserDetails(savedMember),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return new MemberResult(savedMember, true);
        }
    }

    /**
     * Member와 신규 가입 여부를 담는 결과 클래스
     */
    @Getter
    public static class MemberResult {
        private final Member member;
        private final boolean isNewMember;

        public MemberResult(Member member, boolean isNewMember) {
            this.member = member;
            this.isNewMember = isNewMember;
        }

        public boolean isNewMember() {
            return isNewMember;
        }
    }
}
