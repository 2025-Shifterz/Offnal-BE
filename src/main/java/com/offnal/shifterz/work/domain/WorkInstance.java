package com.offnal.shifterz.work.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "work_instance")
public class WorkInstance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //근무 날짜
    private String workDay;

    //근무 유형
    @Enumerated(EnumType.STRING)
    private WorkTimeType workTimeType;

    //근무표와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_calendar")
    private WorkCalendar workCalendar;
}
