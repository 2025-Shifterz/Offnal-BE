package com.offnal.shifterz.work.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "work_schedules")
public class WorkCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // 근무표 이름

    private String year;

    private String month;

    @ElementCollection
    @CollectionTable(name = "work_times", joinColumns = @JoinColumn(name = "work_sch_id"))
    private List<WorkTime> workTimes;

    private String workGroup; // 근무 조 (예: A조, B조)
}

