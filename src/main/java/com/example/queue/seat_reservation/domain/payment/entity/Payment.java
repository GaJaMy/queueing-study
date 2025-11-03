package com.example.queue.seat_reservation.domain.payment.entity;

import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_reservation", columnList = "reservation_id", unique = true),
    @Index(name = "idx_payment_user", columnList = "user_id"),
    @Index(name = "idx_payment_paid_at", columnList = "paid_at"),
    @Index(name = "idx_user_paid_at", columnList = "user_id, paid_at")
})
public class Payment {
    @Id
    private String paymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "total_amount", nullable = false)
    @Positive(message = "결제 총액은 양수여야 합니다.")
    private Integer totalAmount;

    @Column(name = "point_used", nullable = false)
    @PositiveOrZero(message = "사용 포인트는 0 이상이어야 합니다.")
    private Integer pointUsed;

    @Column(name = "cash_used", nullable = false)
    @PositiveOrZero(message = "사용 현금은 0 이상이어야 합니다.")
    private Integer cashUsed;

    @Column(name = "point_earned", nullable = false)
    @PositiveOrZero(message = "적립 포인트는 0 이상이어야 합니다.")
    private Integer pointEarned;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "paid_at", updatable = false, nullable = false)
    private LocalDateTime paidAt;
}
