package com.example.ticketingSystem.domain.ticketing.entity;

public enum SeatStatus {
    AVAILABLE,  // 판매 가능
    RESERVED,   // 예약 중 (결제 대기)
    SOLD        // 판매 완료
}