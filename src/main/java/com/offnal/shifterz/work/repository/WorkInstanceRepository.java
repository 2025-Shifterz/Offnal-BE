package com.offnal.shifterz.work.repository;

import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.domain.WorkInstance;
import com.offnal.shifterz.work.domain.WorkTimeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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

    @Query("""
        select wi
        from WorkInstance wi
        join wi.workCalendar wc
        where wi.workDate = :workDate
          and wc.memberId = :memberId
    """)
    Optional<WorkInstance> findByWorkDateAndMemberIdThroughWorkCalendar(
            @Param("workDate") LocalDate workDate,
            @Param("memberId") Long memberId
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM WorkInstance wi WHERE wi.workCalendar.memberId = :memberId")
    void deleteByMemberId(Long memberId);

    @Query("""
    select wi
    from WorkInstance wi
    join fetch wi.workCalendar wc
    join fetch wc.organization o
    where o.id = :organizationId
    and (:startDate is null or wi.workDate >= :startDate)
    and (:endDate is null or wi.workDate <= :endDate)
    order by wi.workDate asc
""")
    List<WorkInstance> findByOrganizationIdAndDateRange(
            @Param("organizationId") Long organizationId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    Optional<WorkInstance> findByWorkCalendarOrganizationAndWorkDate(Organization organization, LocalDate date);


    @Query("""
    SELECT wi.workTimeType
    FROM WorkInstance wi
    WHERE wi.workDate = :date
      AND wi.workCalendar.memberId = :memberId
      AND wi.workCalendar.organization = :organization
""")
    Optional<WorkTimeType> findTypeByDateAndMemberAndOrganization(
            @Param("date") LocalDate date,
            @Param("memberId") Long memberId,
            @Param("organization") Organization organization
    );

    @Query("""
    SELECT wi FROM WorkInstance wi
    WHERE wi.workDate = :date
      AND wi.workCalendar.memberId= :memberId
      AND wi.workCalendar.organization = :organization
""")
    Optional<WorkInstance> findByWorkDateAndMemberIdAndOrganization(
            @Param("date") LocalDate date,
            @Param("memberId") Long memberId,
            @Param("organization") Organization organization);

}
