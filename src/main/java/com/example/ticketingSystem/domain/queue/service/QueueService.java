package com.example.ticketingSystem.domain.queue.service;

import com.example.ticketingSystem.domain.queue.dto.QueueStatusDto;
import com.example.ticketingSystem.domain.queue.repository.WaitingQueueRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {

    // [변경] RedisQueueRepository -> WaitingQueueRepository (인터페이스 사용)
    private final WaitingQueueRepository queueRepository;

    public void enterQueue(String eventId, Long userId) {
        if (queueRepository.isActive(eventId, userId)) {
            return;
        }
        queueRepository.registerWaitQueue(eventId, userId);
        log.info("User {} entered waiting queue for event {}", userId, eventId);
    }

    public QueueStatusDto getQueueStatus(String eventId, Long userId) {
        if (queueRepository.isActive(eventId, userId)) {
            return new QueueStatusDto(0L, true);
        }

        Long rank = queueRepository.getRank(eventId, userId);
        if (rank == null) {
            return new QueueStatusDto(-1L, false);
        }

        return new QueueStatusDto(rank + 1, false);
    }

    public boolean isUserActive(String eventId, Long userId) {
        return queueRepository.isActive(eventId, userId);
    }

    // 토큰 만료 처리
    // - 결제가 완료되면 대기열(활성 유저 목록)에서 해당 유저를 제거
    public void expireToken(String eventId, Long userId) {
        queueRepository.removeActiveUser(eventId, userId);
    }
}

/*
나중에 Redis 대신 Kafka나 DB로 대기열 구현 방식을 바꾸고 싶을 떄 :
1. KafkaQueueRepository를 새로 만들어서 WaitingQueueRepository를 구현
2. RedisQueueRepository는 삭제
-> QueueService 코드는 단 한 줄도 수정할 필요가 없음.
   (의존성 역전 원칙(DIP, Dependency Inversion Principle) 적용)
 */
