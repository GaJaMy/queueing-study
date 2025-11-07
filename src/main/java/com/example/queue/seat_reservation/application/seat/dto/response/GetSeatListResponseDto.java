package com.example.queue.seat_reservation.application.seat.dto.response;

import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class GetSeatListResponseDto {
    @Schema(description = "좌석 리스트")
    private List<SeatInfo> seats;

    @Schema(description = "전체 좌석 수", example = "55")
    private int totalCount;

    @Schema(description = "예약 가능한 좌석 수", example = "30")
    private int availableCount;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SeatInfo {
        @Schema(description = "좌석 아이디", example = "A-001")
        private String seatId;

        @Schema(description = "좌석 번호", example = "A-1")
        private String seatNumber;

        @Schema(description = "좌석 가격", example = "50000")
        private int price;

        @Schema(description = "좌석 상태", example = "AVAILABLE", allowableValues = "AVAILABLE, TEMP_RESERVED, RESERVED")
        private SeatStatus status;
    }
}
