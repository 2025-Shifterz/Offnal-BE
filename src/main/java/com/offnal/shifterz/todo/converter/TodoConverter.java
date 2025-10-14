package com.offnal.shifterz.todo.converter;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.todo.domain.Todo;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;

import java.time.LocalDate;
import java.util.Optional;

public class TodoConverter {

    public static Todo toEntity(TodoRequestDto.CreateDto request, Member member, Organization organization) {
        LocalDate targetDate = Optional.ofNullable(request.getTargetDate())
                .orElse(LocalDate.now());

        return Todo.builder()
                .content(request.getContent())
                .completed(request.getCompleted())
                .targetDate(targetDate)
                .member(member)
                .organization(organization)
                .build();
    }


    public static TodoResponseDto.TodoDto toDto(Todo todo) {
        return TodoResponseDto.TodoDto.builder()
                .id(todo.getId())
                .content(todo.getContent())
                .completed(todo.getCompleted())
                .targetDate(todo.getTargetDate())
                .organizationId(
                        todo.getOrganization() != null ? todo.getOrganization().getId() : null
                )
                .build();
    }
}
