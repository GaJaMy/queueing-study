package com.example.queue.seat_reservation.domain.queueToken.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Redis에 저장되는 대기열 토큰
 * - RedisTemplate으로 직접 관리
 * - @RedisHash 제거 (제약 회피)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueToken {
    private String token;
    private String userId;
    private QueueTokenStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;

    public void modifyStatus(QueueTokenStatus status) {
        this.status = status;
    }

    public void modifyActivatedAt(LocalDateTime activatedAt) {
        this.activatedAt = activatedAt;
    }
}
