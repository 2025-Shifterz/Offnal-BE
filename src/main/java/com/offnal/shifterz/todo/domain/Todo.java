package com.offnal.shifterz.todo.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.organization.domain.Organization;
import com.offnal.shifterz.todo.dto.TodoRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isSuccess = false; // 기본값 false

    @Column(nullable = false)
    private LocalDate targetDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization; // nullable

    public void update(TodoRequestDto.UpdateDto request) {
        if (request.getContent() != null) this.content = request.getContent();
        if (request.getIsSuccess() != null) this.isSuccess = request.getIsSuccess();
        if (request.getTargetDate() != null) this.targetDate = request.getTargetDate();
    }
}

