package com.offnal.shifterz.log.repository;

import com.offnal.shifterz.log.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LogRepository extends JpaRepository<Log, Long> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Log l SET l.member = NULL WHERE l.member.id = :memberId")
    void anonymizeMemberLogs(@Param("memberId") Long memberId);
}

