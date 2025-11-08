package com.offnal.shifterz.memo.repository;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.domain.Memo;
import com.offnal.shifterz.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findAllByMember(Member member);
    List<Memo> findAllByMemberAndOrganization(Member member, Organization organization);
    List<Memo> findAllByMemberAndOrganizationIsNull(Member member);
}
