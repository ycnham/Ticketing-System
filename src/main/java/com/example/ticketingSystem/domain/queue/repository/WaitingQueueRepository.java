package com.example.ticketingSystem.domain.queue.repository;

import java.util.Set;

public interface WaitingQueueRepository {
    // 대기열 등록
    Boolean registerWaitQueue(String eventId, Long userId);

    // 진입 처리 (대기열 제거 + 활성 토큰 발급)
    void activateUser(String eventId, Long userId);

    // 대기 순번 조회
    Long getRank(String eventId, Long userId);

    // 진입 가능한 유저 조회 (Range)
    Set<Object> popMin(String eventId, long count);

    // 활성화 여부 확인
    boolean isActive(String eventId, Long userId);

    // 토큰 만료 (삭제) 메서드 선언
    void removeActiveUser(String eventId, Long userId);
}