package com.example.queue.seat_reservation.application.wallet.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class GetWalletInfoResponseDto {
    @Schema(description = "지갑 아이디", example = "wallet-001")
    private long walletId;

    @Schema(description = "유저 아이디", example = "user-001")
    private String userId;

    @Schema(description = "현금", example = "100000")
    private int cash;

    @Schema(description = "포인트", example = "1000")
    private int point;

    @Schema(description = "마지막 갱신 시간", example = "2024-01-01T12:00:00")
    private LocalDateTime updatedAt;
}
