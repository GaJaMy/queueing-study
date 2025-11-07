package com.example.queue.seat_reservation.interfaces.wallet.swagger;

import com.example.queue.seat_reservation.application.wallet.dto.response.GetWalletInfoResponseDto;
import com.example.queue.seat_reservation.infrastructure.wallet.dto.request.ChargeCashRequestDto;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "지갑 관련 API", description = "현금 충전 및 포인트 관련 API 담당")
public interface WalletControllerDocs {
    @Operation(
            summary = "현금 충전 API",
            description = "충전 금액을 받아 지갑의 현금을 충전,",
            responses = {
                    @ApiResponse(responseCode = "200", description = "현금 충전 성공", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<ResponseDto<Void>> chargeCash(
            @Valid @RequestHeader("X-Queue-Token") String token,
            @Valid @RequestBody ChargeCashRequestDto chargeCashRequestDto
    );

    @Operation(
            summary = "잔액 조회 API",
            description = "현재 지갑의 잔액을 조회하는 API,",
            responses = {
                    @ApiResponse(responseCode = "200", description = "지갑 잔액 조회 성공", useReturnTypeSchema = true)
            }
    )
     ResponseEntity<ResponseDto<GetWalletInfoResponseDto>> getWalletInfo(
            @Valid @RequestHeader("X-Queue-Token") String token
    );
}
