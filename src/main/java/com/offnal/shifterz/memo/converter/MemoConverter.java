package com.offnal.shifterz.memo.converter;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.domain.Memo;
import com.offnal.shifterz.memo.dto.MemoRequestDto;
import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.organization.domain.Organization;

import java.time.LocalDate;
import java.util.Optional;

public class MemoConverter {

    public static Memo toEntity(MemoRequestDto.CreateDto request, Member member, Organization organization) {
        LocalDate targetDate = Optional.ofNullable(request.getTargetDate())
                .orElse(LocalDate.now());

        return Memo.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .targetDate(targetDate)
                .member(member)
                .organization(organization)
                .build();
    }

    public static MemoResponseDto.MemoDto toDto(Memo memo) {
        return MemoResponseDto.MemoDto.builder()
                .id(memo.getId())
                .title(memo.getTitle())
                .content(memo.getContent())
                .targetDate(memo.getTargetDate())
                .organizationId(
                        memo.getOrganization() != null ? memo.getOrganization().getId() : null
                )
                .build();
    }
}
