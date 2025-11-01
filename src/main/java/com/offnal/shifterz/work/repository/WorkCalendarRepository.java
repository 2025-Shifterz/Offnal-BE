package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkCalendarRepository extends JpaRepository<WorkCalendar, Long> {
    boolean existsByMemberIdAndOrganizationAndStartDateAndEndDate(
            Long memberId, Organization organization, LocalDate startDate, LocalDate  endDate);

    Optional<WorkCalendar> findByMemberIdAndOrganizationAndStartDateLessThanEqualAndEndDateGreaterThanEqual(
            Long memberId,Organization organization, LocalDate startDate, LocalDate  endDate);

    Optional<WorkCalendar> findByMemberIdAndOrganizationAndCalendarName(
            Long memberId,
            Organization organization,
            String calendarName
    );

    Optional<WorkCalendar> findByMemberIdAndOrganization_OrganizationNameAndOrganization_TeamAndCalendarName(
            Long memberId,
            String organizationName,
            String team,
            String calendarName
    );
}
