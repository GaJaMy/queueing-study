package com.example.queue.seat_reservation.application.payment.dto.response;

import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PayResponseDto {
    private String paymentId;
    private String reservationId;
    private String seatId;
    private String seatNumber;
    private SeatStatus status;
    private int Amount;
    private PaymentDetail payment;
    private BalanceDetail balance;
    private String paidAt;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class PaymentDetail {
        private int pointUsed;
        private int cashUsed;
        private int pointEarned;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class BalanceDetail {
        private int remainingCash;
        private int remainingPoint;
    }
}
