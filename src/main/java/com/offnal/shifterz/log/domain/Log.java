package com.offnal.shifterz.log.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import com.offnal.shifterz.member.domain.Member;

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

