package com.example.queue.seat_reservation.domain.payment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HistoryType {
    CHARGE("충전"),
    PAYMENT_CASH("현금 결제"),
    PAYMENT_POINT("포인트 결제"),
    POINT_EARN("포인트 적립")
    ;

    private final String description;
}
