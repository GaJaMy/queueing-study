package com.example.queue.seat_reservation.interfaces.seat.controller;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.seat.dto.response.GetSeatListResponseDto;
import com.example.queue.seat_reservation.application.seat.usecase.SeatUseCase;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import com.example.queue.seat_reservation.interfaces.common.util.ResponseBuilder;
import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import com.example.queue.seat_reservation.interfaces.seat.swagger.SeatControllerDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVerSion.V1 + "/seats")
@RequiredArgsConstructor
@Validated
public class SeatController implements SeatControllerDocs {
    private final SeatUseCase seatUseCase;

    @GetMapping
    public ResponseEntity<ResponseDto<GetSeatListResponseDto>> getSeats(
            @Valid @RequestHeader("X-Queue-Token") String token
    ) {
        GetSeatListResponseDto seatList = seatUseCase.getSeatList(token);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, seatList);
    }

    @PostMapping("temp-reserve")
    public void tempReserveSeat() {

    }
}
