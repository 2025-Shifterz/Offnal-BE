package com.offnal.shifterz.log.service;

import com.offnal.shifterz.log.domain.Log;
import com.offnal.shifterz.log.repository.LogRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.offnal.shifterz.member.domain.Member;


@Service
@RequiredArgsConstructor
@Slf4j
public class LogService {

    private final LogRepository logRepository;


    /**
     * 로그 저장
     * @param member 현재 요청한 사용자
     * @param action 수행 동작 (예: 'C' = Controller Enter, 'R' = Return, 'E' = Error)
     * @param message 로그 메시지
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveLog(Member member, Character action, String message) {
        try {
            Log log = Log.builder()
                    .member(member)
                    .action(action)
                    .time(System.currentTimeMillis())
                    .message(message)
                    .build();

            logRepository.save(log);

        } catch (Exception e) {

            log.error("로그 저장 실패: {}", message, e);
        }
    }


}

