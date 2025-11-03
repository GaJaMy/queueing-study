package com.example.queue.seat_reservation.domain.reservation.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReservationStatus {
    EXPIRED("만료된 예약"),
    TEMP_RESERVED("임시 예약"),
    CONFIRMED("확정된 예약"),
    CANCELLED("취소된 예약");

    private final String description;
}
