package com.example.ticketingSystem.domain.ticketing.controller;

import com.example.ticketingSystem.domain.ticketing.dto.BookingRequest;
import com.example.ticketingSystem.domain.ticketing.dto.BookingResponse;
import com.example.ticketingSystem.domain.payment.dto.PaymentRequest;
import com.example.ticketingSystem.application.facade.TicketingFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ticketing")
@RequiredArgsConstructor
public class TicketingController {

    private final TicketingFacade ticketingFacade;

    /**
     * 1️⃣ 좌석 예약 API
     * 기능: 대기열 토큰 검증 -> 좌석 선점 -> 예약 생성 (PENDING)
     * URL: POST /api/v1/ticketing/reserve
     */
    @PostMapping("/reserve")
    public ResponseEntity<BookingResponse> reserveSeat(@RequestBody BookingRequest request) {
        BookingResponse response = ticketingFacade.reserveSeat(request);
        return ResponseEntity.ok(response);
    }

    /**
     * 2️⃣ 결제 API
     * 기능: 예약 내역 확인 -> 유저 포인트 차감 -> 결제 확정 (PAID) -> 대기열 토큰 만료
     * URL: POST /api/v1/ticketing/payment
     */
    @PostMapping("/payment")
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        ticketingFacade.processPayment(request);
        return ResponseEntity.ok("결제가 정상적으로 완료되었습니다. (예약 확정)");
    }
}