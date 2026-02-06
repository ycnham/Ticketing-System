package com.example.ticketingSystem.infrastructure.redis;

import com.example.ticketingSystem.domain.queue.repository.WaitingQueueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class RedisQueueRepository implements WaitingQueueRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    // 1. 대기열 등록 (ZSet 사용)
    @Override
    public Boolean registerWaitQueue(String eventId, Long userId) {
        long score = System.currentTimeMillis();
        // 저장할 때 String 변환
        return redisTemplate.opsForZSet().add(getWaitingKey(eventId), userId.toString(), score);
    }

    // 2. 대기열 활성화 (ZSet 제거 -> Set 추가)
    @Override
    public void activateUser(String eventId, Long userId) {
        // 2-1. 대기열(Waiting Queue)에서 제거
        redisTemplate.opsForZSet().remove(getWaitingKey(eventId), userId.toString());

        // 2-2. 활성 열(Active Queue)에 추가
        // [수정됨] 기존 Value 방식 -> Set 방식으로 변경
        // 저장할 때 String 변환
        redisTemplate.opsForSet().add(getActiveKey(eventId), userId.toString());
    }

    // 3. 대기 순번 조회
    @Override
    public Long getRank(String eventId, Long userId) {
        return redisTemplate.opsForZSet().rank(getWaitingKey(eventId), userId.toString());
    }

    // 4. 진입 가능 유저 조회 (스케줄러용)
    @Override
    public Set<Object> popMin(String eventId, long count) {
        return redisTemplate.opsForZSet().range(getWaitingKey(eventId), 0, count - 1);
    }

    // 5. 활성화 여부 확인
    @Override
    public boolean isActive(String eventId, Long userId) {
        // [수정됨] hasKey -> isMember (Set 안에 포함되어 있는지 확인)
        // 조회할 때 String 변환
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(getActiveKey(eventId), userId.toString()));
    }

    // 6. 토큰 만료 (삭제)
    @Override
    public void removeActiveUser(String eventId, Long userId) {
        // [수정됨] 활성 유저 목록(Set)에서 제거
        // 삭제할 때 String 변환
        redisTemplate.opsForSet().remove(getActiveKey(eventId), userId.toString());
    }

    // ==========================================
    // Private Helper Methods
    // ==========================================

    private String getWaitingKey(String eventId) {
        return "queue:waiting:" + eventId;
    }

    // [수정됨] 활성 키는 이제 '특정 유저'가 아니라 '이벤트 전체'를 가리킴
    private String getActiveKey(String eventId) {
        return "active_queue:" + eventId;
    }
}