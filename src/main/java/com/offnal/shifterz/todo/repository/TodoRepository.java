package com.offnal.shifterz.todo.repository;

import com.offnal.shifterz.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findByTargetDateAndMemberId(Long targetDate, Long memberId);
}