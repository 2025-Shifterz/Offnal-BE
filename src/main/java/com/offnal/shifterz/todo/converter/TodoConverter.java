package com.offnal.shifterz.todo.converter;

import java.time.LocalDate;
import java.util.Optional;

import com.offnal.shifterz.global.util.encrypt.EncryptUtil;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.todo.domain.Todo;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;

public class TodoConverter {

	public static Todo toEntity(TodoRequestDto.CreateDto request, String contentEnc, Member member,
		Organization organization) {
		LocalDate targetDate = Optional.ofNullable(request.getTargetDate())
			.orElse(LocalDate.now());

		return Todo.builder()
			.content(contentEnc)
			.completed(request.getCompleted())
			.targetDate(targetDate)
			.member(member)
			.organization(organization)
			.build();
	}

	public static TodoResponseDto.TodoDto toDto(Todo todo, EncryptUtil encryptUtil) {
		String content = null;
		if (todo.getContent() != null && !todo.getContent().isBlank()) {
			content = encryptUtil.decryptAES(todo.getContent());
		}

		return TodoResponseDto.TodoDto.builder()
			.id(todo.getId())
			.content(content)
			.completed(todo.getCompleted())
			.targetDate(todo.getTargetDate())
			.organizationId(
				todo.getOrganization() != null ? todo.getOrganization().getId() : null
			)
			.build();
	}
}
