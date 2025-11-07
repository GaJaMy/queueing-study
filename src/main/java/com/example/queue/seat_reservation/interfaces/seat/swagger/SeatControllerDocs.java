package com.example.queue.seat_reservation.interfaces.seat.swagger;

import com.example.queue.seat_reservation.application.seat.dto.response.GetSeatListResponseDto;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;

public interface SeatControllerDocs {
    @Operation(
            summary = "좌성 상태 전체 조회 API",
            description = "현재 남아 있는 좌석 수와, 좌석 아이디를 반환한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "좌석 조회 성공", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<ResponseDto<GetSeatListResponseDto>> getSeats(
            @Valid @RequestHeader("X-Queue-Token") String token
    );

    @Operation(
            summary = "좌석 임시 예약 API",
            description = "결제 전 좌석을 임시 예약 하는 API",
            responses = {
                    @ApiResponse(responseCode = "200", description = "임시 예약 성공", useReturnTypeSchema = true)
            }
    )
    void tempReserveSeat();
}
