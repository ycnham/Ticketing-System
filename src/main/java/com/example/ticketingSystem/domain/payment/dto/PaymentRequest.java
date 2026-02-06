package com.example.ticketingSystem.domain.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    private Long userId;
    private Long bookingId;
    private Long eventId; // 토큰 만료용
    private Integer amount;
}