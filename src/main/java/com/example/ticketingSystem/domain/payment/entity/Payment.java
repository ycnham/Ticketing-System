package com.example.ticketingSystem.domain.payment.entity;

import com.example.ticketingSystem.domain.ticketing.entity.Booking;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "payment")
@EntityListeners(AuditingEntityListener.class) // [추가] 자동 시간 주입
public class Payment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    // [기존 유지] Booking 객체와 1:1 관계 (FK)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", unique = true, nullable = false)
    private Booking booking;

    // [추가] 인덱싱 및 조회 성능을 위해 User ID 명시적 컬럼 추가
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "transaction_id")
    private String transactionId;

    @Column(nullable = false)
    private Long amount; // 기존 코드의 Long 타입 유지

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status; // ENUM: COMPLETED, FAILED, REFUNDED

    @CreatedDate // [추가] 저장 시 시간 자동 설정
    @Column(name = "paid_at", nullable = false, updatable = false)
    private LocalDateTime paidAt;

    // [추가] 서비스 로직에서 사용하기 위한 빌더
    @Builder
    public Payment(Booking booking, Long userId, Long amount) {
        this.booking = booking;
        this.userId = userId;
        this.amount = amount;
        this.status = PaymentStatus.COMPLETED; // 생성 시 기본값 완료 처리 (가정)
        this.transactionId = UUID.randomUUID().toString(); // 임의의 거래 ID 생성
    }
}