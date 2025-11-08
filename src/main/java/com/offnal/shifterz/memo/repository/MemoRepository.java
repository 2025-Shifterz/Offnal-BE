package com.offnal.shifterz.memo.repository;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.domain.Memo;
import com.offnal.shifterz.organization.domain.Organization;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemoRepository extends JpaRepository<Memo, Long> {
    List<Memo> findAllByMember(Member member);
    List<Memo> findAllByMemberAndOrganization(Member member, Organization organization);
    List<Memo> findAllByMemberAndOrganizationIsNull(Member member);
    @Query("SELECT m FROM Memo m WHERE m.member = :member AND DATE(m.targetDate) = :targetDate")
    List<Memo> findAllByMemberAndCreatedDate(@Param("member") Member member,
                                             @Param("targetDate") LocalDate targetDate);
}
