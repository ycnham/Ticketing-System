package com.example.ticketingSystem.domain.ticketing.repository;

import com.example.ticketingSystem.domain.ticketing.entity.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EventSeatRepository extends JpaRepository<EventSeat, Long> {

    // 단순 조회 (낙관적 락은 @Version 필드가 있어서 별도 Lock 쿼리 필요 없음)
    // 비관적 락을 쓸 경우엔 @Lock(LockModeType.PESSIMISTIC_WRITE)를 붙임
    Optional<EventSeat> findById(Long id);
}