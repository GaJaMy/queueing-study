package com.example.queue.seat_reservation.domain.seat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SeatStatus {
    AVAILABLE("이용 가능 좌석"),
    TEMP_RESERVED("임시 예약된 좌석"),
    CONFIRMED("예약 확정 좌석"),;

    private final String description;
}
