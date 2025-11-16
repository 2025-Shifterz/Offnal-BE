package com.offnal.shifterz.member.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.global.util.RedisUtil;
import com.offnal.shifterz.global.util.S3Service;
import com.offnal.shifterz.global.util.dto.PresignedUrlResponse;
import com.offnal.shifterz.jwt.TokenService;
import com.offnal.shifterz.log.domain.Log;
import com.offnal.shifterz.log.repository.LogRepository;
import com.offnal.shifterz.member.converter.MemberConverter;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.domain.Provider;
import com.offnal.shifterz.member.dto.MemberRequestDto;
import com.offnal.shifterz.member.dto.MemberResponseDto;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.memo.repository.MemoRepository;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import com.offnal.shifterz.todo.repository.TodoRepository;
import com.offnal.shifterz.work.repository.WorkCalendarRepository;
import com.offnal.shifterz.work.repository.WorkInstanceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final S3Service s3Service;
    private final MemoRepository memoRepository;
    private final TodoRepository todoRepository;
    private final OrganizationRepository organizationRepository;
    private final LogRepository logRepository;
    private final RedisUtil redisUtil;
    private final WorkInstanceRepository workInstanceRepository;
    private final WorkCalendarRepository workCalendarRepository;
    private final TokenService tokenService;

    /**
     * 소셜 로그인 회원 등록 또는 업데이트
     * @param provider 소셜 제공자 (KAKAO, GOOGLE, NAVER ...)
     * @param providerId 제공자 ID
     * @param email 이메일
     * @param memberName 이름
     * @param phoneNumber 전화번호
     * @return 등록/업데이트된 Member와 신규 가입 여부
     */
    @Transactional
    public MemberResponseDto.MemberRegisterResponseDto registerMemberIfAbsent(
            Provider provider,
            String providerId,
            String email,
            String memberName,
            String phoneNumber,
            String socialProfileImageUrl
    ) {
        Optional<Member> existingMember = memberRepository.findByProviderAndProviderId(provider, providerId);

        if (existingMember.isPresent()) {
            // 기존 회원 정보 유지
            return MemberConverter.toRegisterResponse(existingMember.get(), false);
        } else {
            // 신규 회원 등록
            Member newMember = Member.builder()
                    .provider(provider)
                    .providerId(providerId)
                    .email(email)
                    .memberName(memberName)
                    .phoneNumber(phoneNumber)
                    .profileImageKey(null)
                    .build();

            Member savedMember = memberRepository.save(newMember);

            if (socialProfileImageUrl != null && !socialProfileImageUrl.isBlank()) {
                uploadSocialProfileImage(savedMember, socialProfileImageUrl);
            }

            return MemberConverter.toRegisterResponse(savedMember, true);
        }
    }

    private void uploadSocialProfileImage(Member member, String socialProfileImageUrl) {
        try{
            String extension = extractExtensionFromUrl(socialProfileImageUrl);

            String key = "profile/" + UUID.randomUUID() + "." + extension;

            byte[] bytes = s3Service.downloadImageFromUrl(socialProfileImageUrl);

            s3Service.uploadImageBytes(bytes, key);

            member.updateMemberInfo(
                    member.getMemberName(),
                    key
            );
        } catch (Exception e) {
            throw new CustomException(S3Service.S3ErrorCode.UPLOAD_TO_S3_FAILED);
        }
    }

    private String extractExtensionFromUrl(String url) {
        try {
            String lower = url.toLowerCase();

            if (lower.contains(".png")) return "png";
            if (lower.contains(".jpeg")) return "jpeg";
            if (lower.contains(".jpg")) return "jpg";

            throw new CustomException(S3Service.S3ErrorCode.UNSUPPORTED_CONTENT_TYPE);
        } catch (Exception e) {
            throw new CustomException(S3Service.S3ErrorCode.UNSUPPORTED_CONTENT_TYPE);
        }
    }

    /**
     * 내 프로필 조회
     */
    @Transactional
    public MemberResponseDto.MemberUpdateResponseDto getMyInfo() {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        MemberResponseDto.MemberUpdateResponseDto response = MemberConverter.toResponse(member);

        String key = member.getProfileImageKey();
        String presignedUrl = null;

        if (key != null && !key.isEmpty()) {
            presignedUrl = s3Service.generateViewPresignedUrl(key);
        }

        return MemberResponseDto.MemberUpdateResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .memberName(member.getMemberName())
                .phoneNumber(member.getPhoneNumber())
                .profileImageKey(key)
                .profileImageUrl(presignedUrl)
                .build();
    }

    /**
     * 회원 탈퇴
     */
    @Transactional
    public void withdrawCurrentMember(HttpServletRequest request) {
        Long memberId = AuthService.getCurrentUserId();

        String anonymized = "deleted_user_" + UUID.randomUUID();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        try {
            String profileImageKey = member.getProfileImageKey();
            if (profileImageKey != null && !profileImageKey.isEmpty()) {
                s3Service.deleteFile(profileImageKey);
            }

            // 로그에서 member 비식별화 (null 처리)
            logRepository.anonymizeMemberLogs(memberId, anonymized);

            memoRepository.deleteByMemberId(memberId);
            todoRepository.deleteByMemberId(memberId);
            workInstanceRepository.deleteByMemberId(memberId);
            workCalendarRepository.deleteByMemberId(memberId);
            organizationRepository.deleteByOrganizationMember_Id(memberId);

            memberRepository.deleteById(memberId);

            redisUtil.delete("RT:" + memberId);

            tokenService.blacklistAccessToken(request);

            Log withdrawLog = Log.builder()
                    .member(null)
                    .action('C')
                    .time(LocalDateTime.now())
                    .message("회원 탈퇴 처리 완료")
                    .anonymizedIdentifier(anonymized)
                    .build();
            logRepository.save(withdrawLog);

        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(MemberErrorCode.MEMBER_WITHDRAW_FAILED);
        }
    }

    /**
     * 일반 정보 수정
     */
    @Transactional
    public MemberResponseDto.MemberUpdateResponseDto updateMemberInfo(MemberRequestDto.MemberUpdateRequestDto request) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateMemberInfo(
                request.getName(),
                member.getProfileImageKey()
        );

        return MemberConverter.toResponse(member);

    }

    /**
     * 사진 정보 추가 또는 수정
     */
    @Transactional
    public void updateProfileImage(String newImageKey) {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String currentKey = member.getProfileImageKey();

        if (currentKey == null || currentKey.isEmpty()) {
            member.updateMemberInfo(
                    member.getMemberName(),
                    newImageKey
            );
        }
    }

    @Transactional
    public void deleteProfileImage() {
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String currentKey = member.getProfileImageKey();

        if (currentKey == null || currentKey.isEmpty()) {
            throw new CustomException(S3Service.S3ErrorCode.S3_KEY_NOT_FOUND);
        }

        s3Service.deleteFile(currentKey);

        member.updateMemberInfo(
                member.getMemberName(),
                null
        );
    }

    @Transactional
    public PresignedUrlResponse generateProfileUploadUrl(String extension){
        Long memberId = AuthService.getCurrentUserId();
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        String existingKey = member.getProfileImageKey();

        PresignedUrlResponse presigned = s3Service.createPresignedUrl(
                extension,
                existingKey
        );

        if (existingKey == null || existingKey.isEmpty()) {
            updateProfileImage(presigned.getKey());
        }

        return presigned;
    }


    @Getter
    @AllArgsConstructor
    public enum MemberErrorCode implements ErrorReason {
        MEMBER_NOT_FOUND("MEM001", HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없습니다."),
        MEMBER_SAVE_FAILED("MEM002", HttpStatus.INTERNAL_SERVER_ERROR, "회원 저장에 실패했습니다."),
        MEMBER_ACCESS_DENIED("MEM003", HttpStatus.FORBIDDEN, "회원 접근 권한이 없습니다."),
        MEMBER_WITHDRAW_FAILED("MEM004", HttpStatus.INTERNAL_SERVER_ERROR, "회원 탈퇴에 실패했습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }

}
