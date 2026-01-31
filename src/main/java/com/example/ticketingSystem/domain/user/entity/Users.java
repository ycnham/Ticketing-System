package com.example.ticketingSystem.domain.user.entity;

import com.example.ticketingSystem.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class Users extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    private Long point = 0L;

    // 생성자 (Builder 패턴 권장하지만 여기선 간소화)
    public Users(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // [비즈니스 로직] 포인트 충전
    // - 0원 이하 충전 시 예외 발생
    public void chargePoint(long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0원보다 커야 합니다.");
        }
        this.point += amount;
    }

    // [비즈니스 메서드] 포인트 사용
    // - 잔액이 부족하면 예외 발생
    // - 잔액이 충분하면 차감
    public void usePoint(Integer amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야 합니다.");
        }

        if (this.point < amount) {
            throw new IllegalStateException("잔액이 부족합니다.");
        }

        this.point -= amount;
    }
}