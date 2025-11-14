package com.offnal.shifterz.organization.repository;


import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findAllByOrganizationMember_Id(Long memberId);

    boolean existsByOrganizationMemberAndOrganizationNameAndTeam(
            Member member, String organizationName, String team
    );

    Optional<Organization> findByOrganizationMember_IdAndOrganizationNameAndTeam(
            Long memberId, String organizationName, String team
    );

    boolean existsByOrganizationMember_IdAndOrganizationNameAndTeam(
            Long memberId, String organizationName, String team
    );

    void deleteByOrganizationMember_Id(Long memberId);
}
