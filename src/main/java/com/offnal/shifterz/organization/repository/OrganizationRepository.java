package com.offnal.shifterz.organization.repository;


import com.offnal.shifterz.organization.domain.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
//    Optional<Organization> findByName(String name);
//
//    List<Organization> findByMembers_Id(Long memberId);

}
