package com.example.queue.seat_reservation.domain.queueToken.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(value = "queue:token")
public class QueueToken {
    @Id
    private String token;

    @TimeToLive
    private Long ttl;

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

    public void modifyTtl(Long ttl) {
        this.ttl = ttl;
    }
}
