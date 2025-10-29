package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class WorkCalendar extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String calendarName;

    private LocalDate startDate;

    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "work_times", joinColumns = @JoinColumn(name = "work_sch_id"))
    private Map<String, WorkTime> workTimes = new HashMap<>();

    // 동시성 제어 - 낙관적 락
    @Version
    private Long version;

    public boolean isOwnedBy(Long memberId, Organization org) {
        if (this.memberId == null || memberId == null) return false;
        if (this.organization == null || org == null) return false;
        return Objects.equals(this.memberId, memberId)
                && Objects.equals(this.organization.getId(), org.getId());
    }

    public boolean contains(LocalDate date) {
        return date != null && !date.isBefore(startDate) && !date.isAfter(endDate);
    }
}