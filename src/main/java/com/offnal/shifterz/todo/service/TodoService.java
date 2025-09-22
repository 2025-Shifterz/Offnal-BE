package com.offnal.shifterz.todo.service;

import com.offnal.shifterz.global.common.AuthService;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.global.exception.ErrorCode;
import com.offnal.shifterz.member.repository.MemberRepository;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.organization.repository.OrganizationRepository;
import com.offnal.shifterz.todo.converter.TodoConverter;
import com.offnal.shifterz.todo.domain.Todo;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import com.offnal.shifterz.todo.dto.TodoResponseDto;
import com.offnal.shifterz.todo.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.offnal.shifterz.member.domain.Member;


//import static com.offnal.shifterz.todo.converter.TodoConverter.toDto;

@Service
@RequiredArgsConstructor
public class TodoService {

//    private final TodoRepository todoRepository;
//
//    private final MemberRepository memberRepository;
//    private final OrganizationRepository organizationRepository;

//    public TodoResponseDto createTodo(TodoRequestDto.CreateDto request) {
//        Member member = AuthService.getCurrentMember();
//
//        Organization organization = null;
//        if (request.getOrganizationId() != null) {
//            organization = organizationRepository.findById(request.getOrganizationId())
//                    .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND));
//        }
//
//        Todo todo = TodoConverter.toEntity(request, member, organization);
//        return TodoConverter.toDto(todoRepository.save(todo));
//    }
//
//
//    public TodoResponseDto updateTodo(Long id, TodoRequestDto.Update request) {
//        Todo todo = todoRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
//
//        Organization organization = null;
//        if (request.getOrganizationId() != null) {
//            organization = organizationRepository.findById(request.getOrganizationId())
//                    .orElseThrow(() -> new CustomException(ErrorCode.ORGANIZATION_NOT_FOUND));
//        }
//
//        TodoConverter.updateEntity(todo, request, organization);
//        return TodoConverter.toDto(todo);
//    }
//
//    public TodoResponseDto getTodo(Long id) {
//        Todo todo = todoRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
//        return TodoConverter.toDto(todo);
//    }
//
//    public void deleteTodo(Long id) {
//        Todo todo = todoRepository.findById(id)
//                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));
//        todoRepository.delete(todo);
//    }

}

