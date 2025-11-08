package com.offnal.shifterz.work.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.global.exception.CustomException;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.work.service.WorkCalendarService;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
public class WorkInstance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //근무 날짜
    private LocalDate workDate;

    //근무 유형
    @Enumerated(EnumType.STRING)
    private WorkTimeType workTimeType;

    //근무표와 매핑
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_calendar_id")
    private WorkCalendar workCalendar;

    // ===== 메서드 =====

    public LocalDate date() { return this.workDate; }
    public WorkTimeType workType() { return this.workTimeType;  }

    public boolean isType(WorkTimeType workType) { return this.workTimeType == workType; }
    public boolean isOn(LocalDate date) { return Objects.equals(this.workDate, date); }

    public void changeType (WorkTimeType workTimeType, Long memberId, Organization org) {
        if (workTimeType == null) return;
        if(!workCalendar.isOwnedBy(memberId, org)) {
            throw new CustomException(WorkCalendarService.WorkCalendarErrorCode.CALENDAR_NOT_FOUND);
        }
        this.workTimeType = workTimeType;
    }

    public static WorkInstance create(WorkCalendar workCalendar, LocalDate date, WorkTimeType type){
        if(workCalendar == null || date == null || type == null || !workCalendar.contains(date)){
            throw new CustomException(WorkCalendarService.WorkCalendarErrorCode.WORK_INSTANCE_NOT_FOUND);
        }
        return WorkInstance.builder()
                .workDate(date)
                .workTimeType(type)
                .workCalendar(workCalendar)
                .build();
    }

}
