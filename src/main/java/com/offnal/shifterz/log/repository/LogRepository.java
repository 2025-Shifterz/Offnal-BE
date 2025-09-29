package com.offnal.shifterz.log.repository;

import com.offnal.shifterz.log.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {
}

