package com.example.ticketingSystem.domain.ticketing.service;

import com.example.ticketingSystem.domain.ticketing.dto.BookingRequest;
import com.example.ticketingSystem.domain.ticketing.dto.BookingResponse;
import com.example.ticketingSystem.domain.queue.service.QueueService;
import com.example.ticketingSystem.domain.ticketing.entity.Booking;
import com.example.ticketingSystem.domain.ticketing.entity.EventSeat;
import com.example.ticketingSystem.domain.ticketing.entity.SeatStatus; // [중요] 패키지 확인
import com.example.ticketingSystem.domain.ticketing.repository.BookingRepository;
import com.example.ticketingSystem.domain.ticketing.repository.EventSeatRepository;
import com.example.ticketingSystem.domain.user.entity.Users;
import com.example.ticketingSystem.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventSeatRepository eventSeatRepository;
    private final UserRepository userRepository;
    private final QueueService queueService;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // 1. 대기열 검증
        String eventIdStr = request.getEventId().toString();
        if (!queueService.isUserActive(eventIdStr, request.getUserId())) {
            throw new IllegalStateException("대기열을 통과하지 않은 사용자입니다.");
        }

        // 2. 사용자 조회
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 좌석 조회
        EventSeat seat = eventSeatRepository.findById(request.getSeatId())
                .orElseThrow(() -> new IllegalArgumentException("좌석을 찾을 수 없습니다."));

        // ️ [디버깅 로그] DB에서 실제로 가져온 값이 무엇인지 확인
        System.out.println(">>> [DEBUG] 좌석 조회 성공: ID=" + seat.getId());
        System.out.println(">>> [DEBUG] DB의 Status: " + seat.getStatus());
        System.out.println(">>> [DEBUG] 비교 대상(Enum): " + SeatStatus.AVAILABLE);
        System.out.println(">>> [DEBUG] 일치 여부(==): " + (seat.getStatus() == SeatStatus.AVAILABLE));

        // 4. 좌석 점유 시도
        // seat.reserve(); // (Booking.createBooking 안에서 이미 먼저 호출하므로 지워야 함)

        // 5. 예매 생성
        Booking booking = Booking.createBooking(user, List.of(seat));
        Booking savedBooking = bookingRepository.save(booking);

        return new BookingResponse(
                savedBooking.getBookingId(),
                savedBooking.getStatus(),
                savedBooking.getTotalAmount(),
                savedBooking.getExpiresAt()
        );
    }
}