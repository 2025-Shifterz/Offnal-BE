package com.offnal.shifterz.memberOrganizationTeam.repository;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberOrganizationTeamRepository
        extends JpaRepository<MemberOrganizationTeam, Long> {
    Optional<MemberOrganizationTeam> findByMemberId(Long memberId);

    Optional<MemberOrganizationTeam> findByMemberAndOrganization(Member member, Organization organization);
    @Query("""
    SELECT mot
    FROM MemberOrganizationTeam mot
    WHERE mot.member.id = :memberId
      AND mot.organization.organizationName = :organizationName
""")
    Optional<MemberOrganizationTeam> findByMemberAndOrganizationName(
            @Param("memberId") Long memberId,
            @Param("organizationName") String organizationName
    );

    void deleteAllByMemberId(Long memberId);
    void deleteAllByOrganization(Organization organization);
}

