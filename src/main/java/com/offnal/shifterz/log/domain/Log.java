package com.offnal.shifterz.log.domain;

import com.offnal.shifterz.global.BaseTimeEntity;
import com.offnal.shifterz.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Log extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 로그 액션 구분
     * C = Controller Enter
     * R = Controller Return
     * S = Service Enter
     * T = Service Return
     * E = Error
     */
    private Character action;

    private Long time;

    @Lob
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Builder
    private Log(Member member, Character action, Long time, String message) {
        this.member = member;
        this.action = action;
        this.time = time;
        this.message = message;
    }

    /**
     * 로그 메시지 갱신
     */
    public void updateMessage(String newMessage) {
        this.message = newMessage;
        this.time = System.currentTimeMillis(); // 수정 시각도 갱신
    }

    /**
     * action 값만 바꾸고 싶을 때
     */
    public void updateAction(Character newAction) {
        this.action = newAction;
        this.time = System.currentTimeMillis();
    }
}

