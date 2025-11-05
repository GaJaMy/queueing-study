package com.example.queue.seat_reservation.domain.token.entity;

import com.example.queue.seat_reservation.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "token_histories", indexes = {
    @Index(name = "idx_token_histories_user", columnList = "user_id"),
    @Index(name = "idx_token_histories_status", columnList = "status"),
    @Index(name = "idx_token_histories_created", columnList = "created_at")
//    @Index(name = "idx_user_status", columnList = "user_id, status")
})
@Entity
@EntityListeners(AuditingEntityListener.class)
public class TokenHistory {
    @Id
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TokenStatus status;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;
}
