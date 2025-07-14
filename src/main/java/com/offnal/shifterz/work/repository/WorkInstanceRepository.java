package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstance, Long> {

    Optional<WorkInstance> findByWorkDayAndWorkCalendar_MemberIdAndWorkCalendar_YearAndWorkCalendar_Month(
            String workDay, Long memberId, String year, String month);

    List<WorkInstance> findByWorkCalendar_MemberIdAndWorkCalendar_YearAndWorkCalendar_Month(
            Long memberId, String year, String month);

    void deleteAllByWorkCalendar(WorkCalendar workCalendar);
}
