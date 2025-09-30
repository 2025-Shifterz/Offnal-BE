package com.offnal.shifterz.todo.repository;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.todo.domain.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByMember(Member member);
    List<Todo> findAllByMemberAndOrganization(Member member, Organization organization);
    List<Todo> findAllByMemberAndOrganizationIsNull(Member member);
}
