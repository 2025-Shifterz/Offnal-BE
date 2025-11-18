package com.offnal.shifterz.memberOrganizationTeam.repository;

import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memberOrganizationTeam.domain.MemberOrganizationTeam;
import com.offnal.shifterz.organization.domain.Organization;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberOrganizationTeamRepository
        extends JpaRepository<MemberOrganizationTeam, Long> {

    Optional<MemberOrganizationTeam> findByMemberAndOrganization(Member member, Organization organization);
}

