package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstance, Long> {

    // 홈에서 사용자의 일정 조회 (조직X)
    Optional<WorkInstance> findByWorkCalendarMemberIdAndWorkDate(Long memberId, LocalDate workDate);

    //조직, 멤버, 날짜 범위로 근무 인스턴스 조회
    List<WorkInstance> findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkDateBetweenOrderByWorkDateAsc(
            Long memberId,
            Organization organization,
            LocalDate startDate,
            LocalDate endDate);

    //근무표 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from WorkInstance wi where wi.workCalendar.id = :calendarId")
    void deleteByWorkCalendarId(@Param("calendarId") Long calendarId);

}
