package com.offnal.shifterz.log.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "log")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Log extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Character action;
    private Long time;

    @Lob
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;
}

