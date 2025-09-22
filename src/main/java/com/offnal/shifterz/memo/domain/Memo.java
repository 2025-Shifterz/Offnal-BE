package com.offnal.shifterz.memo.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "memo")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Memo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private Long targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizationI_id")
    private Organization organization; // nullable
}

