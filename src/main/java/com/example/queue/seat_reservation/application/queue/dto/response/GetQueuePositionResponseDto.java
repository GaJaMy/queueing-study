package com.example.queue.seat_reservation.application.queue.dto.response;

import com.example.queue.seat_reservation.domain.token.entity.TokenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetQueuePositionResponseDto {
    private String token;
    private TokenStatus status;
    private int queuePosition;
    private int remainingWaitCount;
    private int estimatedWaitTime;
    private LocalDateTime expiresAt;
}
