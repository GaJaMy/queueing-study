package com.example.queue.seat_reservation.application.payment.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PayRequestDto {
    private String reservationId;
    private boolean usePoint;
}
