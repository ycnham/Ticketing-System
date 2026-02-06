package com.example.ticketingSystem.domain.ticketing.dto;

import com.example.ticketingSystem.domain.ticketing.entity.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private BookingStatus status;
    private Long totalAmount;
    private LocalDateTime expiresAt; // 결제 마감 시간
}