package com.example.ticketingSystem.domain.ticketing.controller;

import com.example.ticketingSystem.domain.ticketing.dto.BookingRequest;
import com.example.ticketingSystem.domain.ticketing.dto.BookingResponse;
import com.example.ticketingSystem.domain.ticketing.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody BookingRequest request) {
        try {
            BookingResponse response = bookingService.createBooking(request);
            return ResponseEntity.ok(response);
        } catch (ObjectOptimisticLockingFailureException e) {
            // 동시성 이슈 발생 시: "이미 선택된 좌석입니다" 메시지 반환
            return ResponseEntity.status(409).body("이미 선택된 좌석입니다. 다른 좌석을 선택해주세요.");
        } catch (IllegalStateException e) {
            // 대기열 미통과 등
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}