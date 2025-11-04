package com.example.queue.seat_reservation.infrastructure.temporaryRepository.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueueToken {
    private String userId;
    private QueueToken status;
    private int queuePosition;
    private LocalDateTime createdAt;
    private LocalDateTime activatedAt;
}
