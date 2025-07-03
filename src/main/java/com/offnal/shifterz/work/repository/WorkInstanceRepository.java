package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.work.domain.WorkInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstance, Long> {

    Optional<WorkInstance> findByWorkDayAndWorkCalendar_MemberIdAndWorkCalendar_YearAndWorkCalendar_Month(
            String workDay, Long memberId, String year, String month);
}
