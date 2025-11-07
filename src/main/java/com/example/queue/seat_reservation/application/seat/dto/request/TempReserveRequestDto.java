package com.example.queue.seat_reservation.application.seat.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TempReserveRequestDto {
    @Schema(description = "좌석 번호", example = "A-001", requiredMode = Schema.RequiredMode.REQUIRED)
    private String seatId;

    @Schema(description = "예약자 아이디", example = "user123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;
}
