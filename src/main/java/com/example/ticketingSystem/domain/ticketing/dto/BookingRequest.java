package com.example.ticketingSystem.domain.ticketing.dto;

import lombok.AllArgsConstructor; // 추가
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor // 테스트에서 new BookingRequest(...) 쓰기 위함
public class BookingRequest {
    private Long userId;
    private Long eventId;
    private Long seatId; // 구매하려는 좌석 ID
}