package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.work.domain.WorkInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstance, Long> {
    List<WorkInstance> findByWorkCalendar_YearAndWorkCalendar_Month(String year, String month);
}

