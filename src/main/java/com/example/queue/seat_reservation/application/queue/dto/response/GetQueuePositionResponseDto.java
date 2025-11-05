package com.example.queue.seat_reservation.application.queue.dto.response;

import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetQueuePositionResponseDto {
    private String token;
    private QueueTokenStatus status;
    private int queuePosition;
    private int remainingWaitCount;
    private int estimatedWaitTime;
}
