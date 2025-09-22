package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "work_calendar")
public class WorkCalendar extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String calendarName;

    private LocalDate startDate;

    private LocalDate endDate;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "work_times", joinColumns = @JoinColumn(name = "work_sch_id"))
    private Map<String, WorkTime> workTimes = new HashMap<>();
}