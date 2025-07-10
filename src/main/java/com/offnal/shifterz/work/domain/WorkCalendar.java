package com.offnal.shifterz.work.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "work_schedules")
public class WorkCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    private String calendarName; // 근무표 이름

    private String year;

    private String month;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "work_times", joinColumns = @JoinColumn(name = "work_sch_id"))
    private Map<String, WorkTime> workTimes = new HashMap<>();

    private String workGroup; // 유저의 근무 조 (예: A조, B조)
}