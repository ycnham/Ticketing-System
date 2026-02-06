package com.example.ticketingSystem.application.facade;

import com.example.ticketingSystem.domain.ticketing.dto.BookingResponse;
import com.example.ticketingSystem.domain.payment.dto.PaymentRequest;
import com.example.ticketingSystem.domain.ticketing.dto.BookingRequest;
import com.example.ticketingSystem.domain.queue.service.QueueService;
import com.example.ticketingSystem.domain.ticketing.service.BookingService;
import com.example.ticketingSystem.domain.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TicketingFacade {

    private final BookingService bookingService;
    private final PaymentService paymentService;
    private final QueueService queueService;

    /**
     * 1단계: 좌석 예약 (임시 점유)
     * - 대기열 검증
     * - 좌석 점유 (동시성 제어)
     * - 임시 배정 (PENDING 상태)
     */
    public BookingResponse reserveSeat(BookingRequest request) {
        // 1. 대기열 토큰 검증 (Active 상태인지)
        if (!queueService.isUserActive(request.getEventId().toString(), request.getUserId())) {
            throw new IllegalStateException("대기열을 통과하지 않은 사용자입니다.");
        }

        // 2. 예약 생성 (BookingService 호출)
        return bookingService.createBooking(request);
    }

    /**
     * 2단계: 결제 및 예약 확정
     * - 포인트 차감 or PG사 결제
     * - 예약 상태 변경 (PENDING -> PAID)
     * - 대기열 토큰 만료 처리
     */
    @Transactional
    public void processPayment(PaymentRequest request) {
        // 1. 결제 처리 (성공 시 예약 상태 변경까지 내부에서 수행)
        paymentService.pay(request);

        // 2. 결제가 완료되었으므로 대기열 토큰 만료(삭제) 처리
        // (사용자는 이제 볼일 다 봤으므로 대기열에서 퇴장)
        queueService.expireToken(request.getEventId().toString(), request.getUserId());

        log.info("결제 완료 및 대기열 토큰 만료: UserID={}", request.getUserId());
    }
}