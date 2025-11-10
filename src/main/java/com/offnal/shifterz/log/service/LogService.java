package com.offnal.shifterz.log.service;

import com.offnal.shifterz.log.domain.Log;
import com.offnal.shifterz.log.repository.LogRepository;
import com.offnal.shifterz.member.domain.Member;
import com.offnal.shifterz.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository logRepository;
    private final MemberRepository memberRepository;


    /**
     * 로그 저장
     * @param member 현재 요청한 사용자
     * @param action 수행 동작 (예: 'C' = Controller Enter, 'R' = Return, 'E' = Error)
     * @param message 로그 메시지
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Member member, Character action, String message) {
        try {
            // 탈퇴한 회원일 경우
            if (member != null && member.getId() != null && !memberRepository.existsById(member.getId())) {
                member = null;
            }
            Log log = Log.builder()
                    .member(member)
                    .action(action)
                    .time(LocalDateTime.now())
                    .message(message)
                    .build();

            logRepository.save(log);

        } catch (Exception e) {

            log.error("로그 저장 실패: {}", message, e);
        }
    }


}

