package com.example.ticketingSystem.domain.payment.service;

import com.example.ticketingSystem.domain.payment.dto.PaymentRequest;
import com.example.ticketingSystem.domain.payment.entity.Payment;
import com.example.ticketingSystem.domain.payment.repository.PaymentRepository;
import com.example.ticketingSystem.domain.ticketing.entity.Booking;
import com.example.ticketingSystem.domain.ticketing.entity.BookingStatus;
import com.example.ticketingSystem.domain.ticketing.repository.BookingRepository;
import com.example.ticketingSystem.domain.user.entity.Users;
import com.example.ticketingSystem.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    @Transactional
    public void pay(PaymentRequest request) {
        // 1. 예약 정보 조회
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("예약 정보를 찾을 수 없습니다."));

        // 2. 예약자 본인 확인
        // (Booking의 User ID와 요청한 User ID 비교)
        if (!booking.getUser().getId().equals(request.getUserId())) {
            throw new IllegalStateException("예약자 본인만 결제할 수 있습니다.");
        }

        // 3. 이미 결제된 건인지 확인
        if (booking.getStatus() == BookingStatus.PAID) {
            throw new IllegalStateException("이미 결제된 예약입니다.");
        }

        // 4. 결제 금액 검증 (예약 금액 vs 요청 금액)
        // [수정] .equals() 대신 .longValue() == .longValue() 로 안전하게 숫자 비교
        long bookingPrice = booking.getTotalAmount().longValue();
        long requestPrice = request.getAmount().longValue();

        if (bookingPrice != requestPrice) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다. (예약: " + bookingPrice + ", 요청: " + requestPrice + ")");
        }

        // 5. 유저 잔액 차감
        Users user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));

        // (주의: Users 엔티티에 usePoint 메서드가 있어야 함)
        user.usePoint(request.getAmount());

        // 6. 결제 내역 저장
        // [수정 포인트] 기존 Payment 엔티티 구조에 맞춰 Booking 객체를 주입하고, Amount를 Long으로 변환
        Payment payment = Payment.builder()
                .booking(booking) // ID가 아니라 객체를 넣습니다.
                .userId(user.getId())
                .amount(Long.valueOf(request.getAmount())) // Integer -> Long 변환
                .build();

        paymentRepository.save(payment);

        // 7. 예약 상태 변경 (PENDING -> PAID)
        // (주의: Booking 엔티티에 completePayment 메서드가 있어야 함)
        booking.completePayment();
    }
}