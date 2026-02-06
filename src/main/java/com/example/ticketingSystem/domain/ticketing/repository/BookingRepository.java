package com.example.ticketingSystem.domain.ticketing.repository;

import com.example.ticketingSystem.domain.ticketing.entity.Booking;
import com.example.ticketingSystem.domain.ticketing.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // 상태(PENDING)와 시간(현재보다 이전)을 기준으로 조회
    List<Booking> findAllByStatusAndExpiresAtBefore(BookingStatus status, LocalDateTime time);
}