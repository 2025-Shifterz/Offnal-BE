package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.work.domain.WorkInstance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkInstanceRepository extends JpaRepository<WorkInstance, Long> {
}
