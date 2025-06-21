package com.offnal.shifterz.member.service;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 카카오 로그인 회원 등록 또는 업데이트
     * @param kakaoId 카카오 ID
     * @param email 이메일
     * @param nickname 닉네임
     * @param profileImageUrl 프로필 이미지 URL
     * @return 등록/업데이트된 Member와 신규 가입 여부
     */
    @Transactional
    public MemberResult registerOrUpdateKakaoMember(Long kakaoId, String email, String nickname, String profileImageUrl) {
        Optional<Member> existingMember = memberRepository.findByKakaoId(kakaoId);

        if (existingMember.isPresent()) {
            // 기존 회원 정보 업데이트
            Member member = existingMember.get();
            member.setEmail(email);
            member.setProfileImageUrl(profileImageUrl);

            return new MemberResult(member, false);
        } else {
            // 신규 회원 등록 (카카오 로그인만 가능)
            Member newMember = new Member();
            newMember.setKakaoId(kakaoId);
            newMember.setEmail(email);
            newMember.setKakaoNickname(nickname);
            newMember.setProfileImageUrl(profileImageUrl);

            Member savedMember = memberRepository.save(newMember);

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
