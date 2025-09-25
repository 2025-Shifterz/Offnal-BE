package com.offnal.shifterz.todo.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorReason;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import com.offnal.shifterz.todo.converter.TodoConverter;
import com.offnal.shifterz.todo.domain.Todo;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;
import com.offnal.shifterz.todo.repository.TodoRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;




@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final OrganizationRepository organizationRepository;

    @Transactional
    public TodoResponseDto.TodoDto createTodo(TodoRequestDto.CreateDto request) {
        Member member = AuthService.getCurrentMember();

        Organization organization = null;
        if (request.getOrganizationId() != null) {
            organization = organizationRepository.findById(request.getOrganizationId())
                    .orElseThrow(() -> new CustomException(TodoErrorCode.ORGANIZATION_NOT_FOUND));
        }

        Todo todo = TodoConverter.toEntity(request, member, organization);
        return TodoConverter.toDto(todoRepository.save(todo));
    }

    @Transactional
    public TodoResponseDto.TodoDto updateTodo(Long id, TodoRequestDto.UpdateDto request) {
        Member member = AuthService.getCurrentMember();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new CustomException(TodoErrorCode.TODO_NOT_FOUND));


        if (!todo.getMember().getId().equals(member.getId())) {
            throw new CustomException(TodoErrorCode.TODO_ACCESS_DENIED);
        }

        todo.update(request);
        return TodoConverter.toDto(todo);
    }

    @Transactional(readOnly = true)
    public TodoResponseDto.TodoDto getTodo(Long id) {
        Member member = AuthService.getCurrentMember();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new CustomException(TodoErrorCode.TODO_NOT_FOUND));

        if (!todo.getMember().getId().equals(member.getId())) {
            throw new CustomException(TodoErrorCode.TODO_ACCESS_DENIED);
        }

        return TodoConverter.toDto(todo);
    }

    @Transactional
    public void deleteTodo(Long id) {
        Member member = AuthService.getCurrentMember();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new CustomException(TodoErrorCode.TODO_NOT_FOUND));

        if (!todo.getMember().getId().equals(member.getId())) {
            throw new CustomException(TodoErrorCode.TODO_ACCESS_DENIED);
        }

        todoRepository.delete(todo);
    }


    @Getter
    @AllArgsConstructor
    public enum TodoErrorCode implements ErrorReason {
        TODO_NOT_FOUND("TODO001", HttpStatus.NOT_FOUND, "할 일을 찾을 수 없습니다."),
        ORGANIZATION_NOT_FOUND("TODO002", HttpStatus.NOT_FOUND, "소속 조직을 찾을 수 없습니다."),
        TODO_SAVE_FAILED("TODO003", HttpStatus.INTERNAL_SERVER_ERROR, "할 일 저장에 실패했습니다."),
        TODO_ACCESS_DENIED("TODO004", HttpStatus.FORBIDDEN, "해당 할 일에 접근 권한이 없습니다.");
        private final String code;
        private final HttpStatus status;
        private final String message;
    }
}
