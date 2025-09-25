package com.offnal.shifterz.organization.repository;


import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    List<Organization> findAllByOrganizationMember(Member member);
    boolean existsByOrganizationName(String organizationName);
}
