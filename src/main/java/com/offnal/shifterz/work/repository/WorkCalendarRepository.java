package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.work.domain.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WorkCalendarRepository extends JpaRepository<WorkCalendar, Long> {
    boolean existsByMemberIdAndStartDateAndEndDate(Long memberId, LocalDate startDate, LocalDate  endDate);
    Optional<WorkCalendar> findByMemberIdAndStartDateAndEndDate(Long memberId, LocalDate startDate, LocalDate  endDate);
}
