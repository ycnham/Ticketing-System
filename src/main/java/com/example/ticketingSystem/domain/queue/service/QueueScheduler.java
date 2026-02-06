package com.example.ticketingSystem.domain.queue.service;

import com.example.ticketingSystem.infrastructure.redis.RedisQueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
@EnableScheduling
@RequiredArgsConstructor
public class QueueScheduler {

    private final RedisQueueRepository queueRepository;

    // 예시용 이벤트 ID (실제로는 DB에서 진행 중인 이벤트를 조회해서 반복문을 돌려야 함)
    private static final String EVENT_ID = "1";

    // 한 번에 입장시킬 인원 수 (트래픽 제어의 핵심)
    private static final int ENTER_BATCH_SIZE = 50;

    @Scheduled(fixedDelay = 1000) // 1초마다 실행
    public void enterUserScheduler() {
        // 1. 대기열에서 입장시킬 유저들을 가져옴 (Score가 가장 낮은(=먼저 온) 순서)
        Set<Object> waitingUsers = queueRepository.popMin(EVENT_ID, ENTER_BATCH_SIZE);

        if (waitingUsers == null || waitingUsers.isEmpty()) {
            return;
        }

        // 2. 유저들을 활성화 상태로 변경 (Waiting -> Active)
        for (Object userObj : waitingUsers) {
            Long userId = Long.valueOf((String) userObj);
            queueRepository.activateUser(EVENT_ID, userId);
            log.info("User {} is activated for event {}", userId, EVENT_ID);
        }
    }
}