package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Table(
        name = "work_calendar",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_org_calendar_name",
                        columnNames = {"organization_id", "calendar_name"}
                )
        }
)
public class WorkCalendar extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private String calendarName;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "work_times", joinColumns = @JoinColumn(name = "work_calendar_id"))
    private Map<String, WorkTime> workTimes = new HashMap<>();

    @OneToMany(mappedBy = "workCalendar",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<WorkInstance> workInstances = new ArrayList<>();

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