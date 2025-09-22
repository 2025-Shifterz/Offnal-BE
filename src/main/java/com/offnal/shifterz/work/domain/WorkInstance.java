package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Table(name = "work_instance")
public class WorkInstance extends BaseTimeEntity {

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
    @JoinColumn(name = "work_calendar_id")
    private WorkCalendar workCalendar;
}
