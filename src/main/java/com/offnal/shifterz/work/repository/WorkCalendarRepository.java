package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WorkCalendarRepository extends JpaRepository<WorkCalendar, Long> {
    boolean existsByMemberIdAndOrganizationAndStartDateAndEndDate(Long memberId, Organization organization, LocalDate startDate, LocalDate  endDate);
    Optional<WorkCalendar> findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Long memberId,Organization organization, LocalDate startDate, LocalDate  endDate);
}
