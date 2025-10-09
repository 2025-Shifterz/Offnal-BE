package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkCalendar;
import com.offnal.shifterz.work.domain.WorkInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkInstanceRepository extends JpaRepository<WorkInstance, Long> {

    @Query("SELECT wi FROM WorkInstance wi " +
            "JOIN wi.workCalendar wc " +
            "WHERE wi.workDate = :workDate " +
            "AND wc.memberId = :memberId " +
            "AND wc.startDate <= :startDate " +
            "AND wc.endDate >= :endDate")
    Optional<WorkInstance> findByWorkDateAndMemberIdThroughWorkCalendar(
            @Param("workDate") LocalDate workDate,
            @Param("memberId") Long memberId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // 조직과 멤버로 전체 근무 인스턴스 조회
    List<WorkInstance> findByWorkCalendarMemberIdAndWorkCalendarOrganization(
            Long memberId,
            Organization organization);

    //조직, 멤버, 날짜 범위로 근무 인스턴스 조회
    List<WorkInstance> findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkCalendarStartDateLessThanEqualAndWorkCalendarEndDateGreaterThanEqual(
            Long memberId,
            Organization organization,
            LocalDate startDate,
            LocalDate endDate);

    //시작일 이후 근무 인스턴스 조회 (endDate >= startDate)
    List<WorkInstance> findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkCalendarEndDateGreaterThanEqual(
            Long memberId,
            Organization organization,
            LocalDate startDate);


    //종료일 이전 근무 인스턴스 조회 (startDate <= endDate)
    List<WorkInstance> findByWorkCalendarMemberIdAndWorkCalendarOrganizationAndWorkCalendarStartDateLessThanEqual(
            Long memberId,
            Organization organization,
            LocalDate endDate);

    void deleteAllByWorkCalendar(WorkCalendar workCalendar);
}
