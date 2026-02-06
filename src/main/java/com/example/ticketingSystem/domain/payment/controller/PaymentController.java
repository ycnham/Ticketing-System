package com.example.ticketingSystem.domain.payment.controller;

import com.example.ticketingSystem.domain.payment.service.PaymentService;
import com.example.ticketingSystem.domain.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<String> processPayment(@RequestBody PaymentRequest request) {
        // 1. 결제 로직 수행 (PG사 연동 등)
        // 2. 성공 시 Booking 상태를 PAID로 변경
        paymentService.pay(request);
        return ResponseEntity.ok("결제가 완료되었습니다.");
    }
}