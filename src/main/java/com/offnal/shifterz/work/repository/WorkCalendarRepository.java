package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    List<WorkCalendar> findByMemberIdAndOrganizationOrderByStartDateDesc(Long memberId, Organization organization);
    @Query("""
        select wc
        from WorkCalendar wc
        where wc.memberId = :memberId
          and :date between wc.startDate and wc.endDate
    """)
    Optional<WorkCalendar> findByMemberIdAndDate(
            @Param("memberId") Long memberId,
            @Param("date") LocalDate date
    );

    @Transactional
    @Modifying
    void deleteByMemberId(Long memberId);
}
