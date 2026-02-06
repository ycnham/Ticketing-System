package com.example.ticketingSystem.domain.ticketing.entity;

import com.example.ticketingSystem.common.entity.BaseEntity;
import com.example.ticketingSystem.domain.user.entity.Users;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "booking", indexes = {
        // 나의 예매 내역 조회 성능 최적화
        @Index(name = "idx_user_booking", columnList = "user_id")
})
public class Booking extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BookingStatus status;

    @Column(name = "total_amount", nullable = false)
    private Long totalAmount;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<BookingItem> bookingItems = new ArrayList<>();

    // 생성자 메서드
    public static Booking createBooking(Users user, List<EventSeat> seats) {
        Booking booking = new Booking();
        booking.user = user;
        booking.status = BookingStatus.PENDING;
        booking.expiresAt = LocalDateTime.now().plusMinutes(5); // 5분 임시 점유

        long sum = 0;
        for (EventSeat seat : seats) {
            seat.reserve(); // 좌석 상태 변경 (AVAILABLE -> RESERVED)
            sum += seat.getPrice();
            booking.addBookingItem(seat);
        }
        booking.totalAmount = sum;
        return booking;
    }

    // 비즈니스 메서드: 결제 완료 처리
    // - 상태를 PENDING -> PAID로 변경
    // - 이미 완료되었거나 취소된 건이면 예외 발생
    public void completePayment() {
        if (this.status != BookingStatus.PENDING) {
            throw new IllegalStateException("결제 가능한 상태가 아닙니다.");
        }
        this.status = BookingStatus.PAID;
    }

    private void addBookingItem(EventSeat eventSeat) {
        BookingItem item = new BookingItem(this, eventSeat);
        this.bookingItems.add(item);
    }

    // 예약 취소 (좌석들도 같이 풀어줌)
    public void cancel() {
        this.status = BookingStatus.CANCELLED;

        // this.seats는 존재하지 않으므로, bookingItems를 순회하며 좌석에 접근해야 함
        for (BookingItem item : this.bookingItems) {
            // BookingItem 안에 있는 EventSeat를 꺼내서 해제
            item.getEventSeat().release();
        }
    }
}