package com.example.queue.seat_reservation.application.seat.dto.response;

import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class TempReserveResponseDto {
    @Schema(description = "예약 아이디", example = "reservation-001")
    private String reservationId;

    @Schema(description = "좌석 아이디", example = "A-001")
    private String seatId;

    @Schema(description = "좌석 번호", example = "A-1")
    private String seatNumber;

    @Schema(description = "예약 상태", example = "TEMP_RESERVED", allowableValues = "TEMP_RESERVED")
    private SeatStatus status;

    @Schema(description = "예약자 아이디", example = "user-123")
    private String userId;

    @Schema(description = "좌석 가격", example = "50000")
    private int price;

    @Schema(description = "임시 예약 시간", example = "2024-01-01T12:00:00")
    private LocalDateTime reservedAt;

    @Schema(description = "임시 예약 만료 시간", example = "2024-01-01T12:30:00")
    private LocalDateTime expiresAt;
}
