package com.example.queue.seat_reservation.interfaces.payment.controller;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.payment.dto.request.PayRequestDto;
import com.example.queue.seat_reservation.application.payment.dto.response.PayResponseDto;
import com.example.queue.seat_reservation.application.payment.service.PaymentService;
import com.example.queue.seat_reservation.application.payment.usecase.PaymentUseCase;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import com.example.queue.seat_reservation.interfaces.common.util.ResponseBuilder;
import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVerSion.V1 + "/payment")
@RequiredArgsConstructor
@Validated
public class PaymentController {
    private final PaymentUseCase paymentUseCase;

    @GetMapping
    public ResponseEntity<ResponseDto<PayResponseDto>> payment(
            @Valid @RequestHeader("X-Queue-Token") String token,
            @Valid @RequestBody PayRequestDto payRequestDto
    ) {
        PayResponseDto pay = paymentUseCase.pay(token, payRequestDto);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, pay);
    }
}
