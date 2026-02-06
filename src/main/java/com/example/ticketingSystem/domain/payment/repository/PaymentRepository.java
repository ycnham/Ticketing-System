package com.example.ticketingSystem.domain.payment.repository;

import com.example.ticketingSystem.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * 특정 예약(Booking)에 대한 결제 내역이 존재하는지 확인
     * 용도: 중복 결제 방지, 결제 상태 확인
     * (Payment 엔티티의 booking 필드의 id를 기준으로 검색)
     */
    Optional<Payment> findByBooking_BookingId(Long bookingId);

    /**
     * 특정 사용자의 결제 내역 전체 조회
     * 용도: 마이페이지 - 결제 히스토리
     */
    List<Payment> findByUserId(Long userId);
}