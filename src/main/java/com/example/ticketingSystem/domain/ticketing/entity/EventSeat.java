package com.example.ticketingSystem.domain.ticketing.entity;

import com.example.ticketingSystem.domain.event.entity.EventSchedule;
import com.example.ticketingSystem.domain.event.entity.Seat;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "event_seat", indexes = {
        // 잔여석 조회 쿼리 성능 최적화 (WHERE schedule_id = ? AND status = ?)
        @Index(name = "idx_schedule_status", columnList = "schedule_id, status")
})
public class EventSeat {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private EventSchedule eventSchedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SeatStatus status;

    @Column(nullable = false)
    private Integer price;

    // 낙관적 락 (Optimistic Lock)
    @Version
    private Long version;

    // [추가된 부분] 테스트 코드 및 객체 생성을 위한 생성자
    public EventSeat(SeatStatus status, Integer price) {
        this.status = status;
        this.price = price;
    }

    // 비즈니스 메서드: 예약 시도
    public void reserve() {
        if (!this.status.name().equals("AVAILABLE")) {
            throw new IllegalStateException("이미 예약된 좌석입니다.");
        }
        this.status = SeatStatus.RESERVED;
    }

    // 비즈니스 메서드: 예약 취소/만료
    public void cancel() {
        this.status = SeatStatus.AVAILABLE;
    }

    // 좌석 점유 해제 (다시 예약 가능한 상태로)
    public void release() {
        this.status = SeatStatus.AVAILABLE;
    }
}