package com.example.queue.seat_reservation.domain.token.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum TokenStatus {
    WAITING("대기열 대기"),
    ACTIVE("토큰 활성화"),
    EXPIRED("토큰 만료"),;

    private final String description;
}
