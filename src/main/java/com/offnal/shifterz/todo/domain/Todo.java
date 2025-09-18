package com.offnal.shifterz.todo.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "todo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private Boolean isSuccess;

    private Long targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization; // nullable
}

