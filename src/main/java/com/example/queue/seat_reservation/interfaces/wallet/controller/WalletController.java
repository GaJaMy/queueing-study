package com.example.queue.seat_reservation.interfaces.wallet.controller;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.wallet.dto.response.GetWalletInfoResponseDto;
import com.example.queue.seat_reservation.application.wallet.service.WalletService;
import com.example.queue.seat_reservation.application.wallet.usecase.WalletUseCase;
import com.example.queue.seat_reservation.infrastructure.wallet.dto.request.ChargeCashRequestDto;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import com.example.queue.seat_reservation.interfaces.common.util.ResponseBuilder;
import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import com.example.queue.seat_reservation.interfaces.wallet.swagger.WalletControllerDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVerSion.V1 + "/wallet")
@RequiredArgsConstructor
@Validated
public class WalletController implements WalletControllerDocs {
    private final WalletUseCase walletUseCase;

    @PostMapping("/charge")
    public ResponseEntity<ResponseDto<Void>> chargeCash(
            @Valid @RequestHeader("X-Queue-Token") String token,
            @Valid @RequestBody ChargeCashRequestDto chargeCashRequestDto
    ) {
        walletUseCase.chargeCash(token, chargeCashRequestDto);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, null);
    }

    @GetMapping
    public ResponseEntity<ResponseDto<GetWalletInfoResponseDto>> getWalletInfo(
            @Valid @RequestHeader("X-Queue-Token") String token
    ) {
        GetWalletInfoResponseDto walletInfo = walletUseCase.getWalletInfo(token);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, walletInfo);
    }
}
