package com.offnal.shifterz.memo.converter;

import java.time.LocalDate;
import java.util.Optional;

import com.offnal.shifterz.global.util.encrypt.EncryptUtil;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.domain.Memo;
import com.offnal.shifterz.memo.dto.MemoRequestDto;
import com.offnal.shifterz.memo.dto.MemoResponseDto;
import com.offnal.shifterz.organization.domain.Organization;

public class MemoConverter {

	public static Memo toEntity(MemoRequestDto.CreateDto request, String titleEnc,
		String contentEnc, Member member, Organization organization) {
		LocalDate targetDate = Optional.ofNullable(request.getTargetDate())
			.orElse(LocalDate.now());

		return Memo.builder()
			.title(titleEnc)
			.content(contentEnc)
			.targetDate(targetDate)
			.member(member)
			.organization(organization)
			.build();
	}

	public static MemoResponseDto.MemoDto toDto(Memo memo, EncryptUtil encryptUtil) {
		return MemoResponseDto.MemoDto.builder()
			.id(memo.getId())
			.title(encryptUtil.decryptAESOrNull(memo.getTitle()))
			.content(encryptUtil.decryptAESOrNull(memo.getContent()))
			.targetDate(memo.getTargetDate())
			.organizationId(
				memo.getOrganization() != null ? memo.getOrganization().getId() : null
			)
			.build();
	}
}
