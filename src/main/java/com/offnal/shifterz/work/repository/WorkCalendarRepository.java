package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.work.domain.WorkCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkCalendarRepository extends JpaRepository<WorkCalendar, Long> {

}
