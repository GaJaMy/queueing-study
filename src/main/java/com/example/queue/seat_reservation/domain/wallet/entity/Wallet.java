package com.example.queue.seat_reservation.domain.wallet.entity;

import com.example.queue.seat_reservation.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "wallets", indexes = {
    @Index(name = "idx_wallet_user", columnList = "user_id", unique = true)
})
public class Wallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long walletId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "cash", nullable = false, columnDefinition = "INT DEFAULT 0")
    @PositiveOrZero(message = "현금은 0 이상이어야 합니다.")
    private Integer cash;

    @Column(name = "point", nullable = false, columnDefinition = "INT DEFAULT 0")
    @PositiveOrZero(message = "포인트는 0 이상이어야 합니다.")
    private Integer point;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
