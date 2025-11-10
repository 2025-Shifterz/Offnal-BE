package com.offnal.shifterz.memo.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.memo.dto.MemoRequestDto;
import com.offnal.shifterz.organization.domain.Organization;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Memo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String title;

    @Lob
    @Column()
    private String content;

    @Column(nullable = false)
    private LocalDate targetDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization; // nullable

    public void update(MemoRequestDto.UpdateMemoDto request) {
        if (request.getContent() != null) this.content = request.getContent();
        if (request.getTargetDate() != null) this.targetDate = request.getTargetDate();
    }
}

