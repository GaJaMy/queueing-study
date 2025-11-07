package com.example.queue.seat_reservation.interfaces.payment.swagger;

import com.example.queue.seat_reservation.application.payment.dto.request.PayRequestDto;
import com.example.queue.seat_reservation.application.payment.dto.response.PayResponseDto;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "결제 관련 API", description = "결제 관련 기능을 담당하는 API")
public interface PaymentControllerDocs {
    @Operation(
            summary = "결제 처리 API",
            description = "결제 요청을 받아 처리한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "결제 성공", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<ResponseDto<PayResponseDto>> payment(
            @Valid @RequestHeader("X-Queue-Token") String token,
            @Valid @RequestBody PayRequestDto payRequestDto
    );
}
