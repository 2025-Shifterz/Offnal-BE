package com.offnal.shifterz.memo.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.converter.MemoConverter;
import com.offnal.shifterz.memo.domain.Memo;
import com.offnal.shifterz.memo.dto.MemoRequestDto;
import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.memo.repository.MemoRepository;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor

public class MemoService {

    private final MemoRepository memoRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public MemoResponseDto.MemoDto createMemo(MemoRequestDto.CreateDto request) {
        Member member = AuthService.getCurrentMember();

        Organization organization = null;
        if (request.getOrganizationId() != null) {
            organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new CustomException(MemoErrorCode.ORGANIZATION_NOT_FOUND));
        }

        Memo memo = MemoConverter.toEntity(request, member, organization);
        return MemoConverter.toDto(memoRepository.save(memo));
    }

    @Transactional
    public MemoResponseDto.MemoDto updateMemo(MemoRequestDto.UpdateMemoDto request) {
        Member member = AuthService.getCurrentMember();

        Memo memo = memoRepository.findById(request.getId())
                .orElseThrow(() -> new CustomException(MemoErrorCode.MEMO_NOT_FOUND));

        if (!memo.getMember().getId().equals(member.getId())) {
            throw new CustomException(MemoErrorCode.MEMO_ACCESS_DENIED);
        }

        memo.update(request);
        return MemoConverter.toDto(memo);
    }

    @Transactional(readOnly = true)
    public MemoResponseDto.MemoDto getMemo(Long id) {
        Member member = AuthService.getCurrentMember();

        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new CustomException(MemoErrorCode.MEMO_NOT_FOUND));

        if (!memo.getMember().getId().equals(member.getId())) {
            throw new CustomException(MemoErrorCode.MEMO_ACCESS_DENIED);
        }

        return MemoConverter.toDto(memo);
    }

    @Transactional
    public void deleteMemo(Long id) {
        Member member = AuthService.getCurrentMember();

        Memo memo = memoRepository.findById(id)
                .orElseThrow(() -> new CustomException(MemoErrorCode.MEMO_NOT_FOUND));

        if (!memo.getMember().getId().equals(member.getId())) {
            throw new CustomException(MemoErrorCode.MEMO_ACCESS_DENIED);
        }

        memoRepository.delete(memo);
    }

    @Getter
    @AllArgsConstructor
    public enum MemoErrorCode implements ErrorReason {
        MEMO_NOT_FOUND("MEMO001", HttpStatus.NOT_FOUND, "메모를 찾을 수 없습니다."),
        ORGANIZATION_NOT_FOUND("MEMO002", HttpStatus.NOT_FOUND, "소속 조직을 찾을 수 없습니다."),
        MEMO_SAVE_FAILED("MEMO003", HttpStatus.INTERNAL_SERVER_ERROR, "메모 저장에 실패했습니다."),
        MEMO_ACCESS_DENIED("MEMO004", HttpStatus.FORBIDDEN, "해당 메모에 접근 권한이 없습니다.");

        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
