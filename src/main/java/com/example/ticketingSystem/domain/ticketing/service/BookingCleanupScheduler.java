package com.example.ticketingSystem.domain.ticketing.service;

import com.example.ticketingSystem.domain.ticketing.entity.BookingStatus;
import com.example.ticketingSystem.domain.ticketing.entity.Booking;
import com.example.ticketingSystem.domain.ticketing.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookingCleanupScheduler {

    private final BookingRepository bookingRepository;

    /**
     * 스케줄러: 1분마다 실행 (60000ms)
     * 역할: 만료 시간(expiresAt)이 지난 PENDING 예약을 찾아 취소 처리
     */
    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();

        // 1. 청소 대상 찾기 (PENDING 상태이면서, 만료 시간이 현재보다 과거인 것)
        List<Booking> expiredBookings = bookingRepository.findAllByStatusAndExpiresAtBefore(
                BookingStatus.PENDING,
                now
        );

        // 2. 대상이 없으면 조용히 종료
        if (expiredBookings.isEmpty()) {
            return;
        }

        log.info("🧹 [스케줄러] 만료된 예약 {}건 발견! 정리 시작...", expiredBookings.size());

        // 3. 하나씩 취소 처리
        for (Booking booking : expiredBookings) {
            booking.cancel(); // 예약 취소 + 좌석 해제
            log.info("   🗑️ 예약 취소 완료: BookingID={}, UserID={}", booking.getBookingId(), booking.getUser().getId());
        }
    }
}