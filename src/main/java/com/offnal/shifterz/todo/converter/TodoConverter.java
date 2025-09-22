package com.offnal.shifterz.todo.converter;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.todo.domain.Todo;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;
import com.offnal.shifterz.member.domain.Member;
import java.time.LocalDate;
import java.util.Optional;

public class TodoConverter {
//    public static Todo toEntity(TodoRequestDto.CreateDto request, Member member, Organization organization) {
//        Long targetDate = Optional.ofNullable(request.getTargetDate())
//                .orElse(LocalDate.now().toEpochDay());
//
//        return Todo.builder()
//                .content(request.getContent())
//                .isSuccess(Optional.ofNullable(request.getIsSuccess()).orElse(false))
//                .targetDate(targetDate)
//                .member(member)
//                .organization(organization)
//                .build();
//    }
//
//    public static TodoResponseDto toDto(Todo todo) {
//        return TodoResponseDto.builder()
//                .id(todo.getId())
//                .content(todo.getContent())
//                .isSuccess(todo.getIsSuccess())
//                .targetDate(todo.getTargetDate())
//                .organizationId(
//                        todo.getOrganization() != null ? todo.getOrganization().getId() : null
//                )
//                .createdAt(todo.getCreatedAt())
//                .updatedAt(todo.getUpdatedAt())
//                .build();
//    }
}
