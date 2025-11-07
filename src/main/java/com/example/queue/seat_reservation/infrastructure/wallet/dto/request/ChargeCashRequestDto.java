package com.example.queue.seat_reservation.infrastructure.wallet.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ChargeCashRequestDto {
    private int amount;
}
