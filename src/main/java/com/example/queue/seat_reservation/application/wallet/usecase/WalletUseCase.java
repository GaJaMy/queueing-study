package com.example.queue.seat_reservation.application.wallet.usecase;

import com.example.queue.seat_reservation.application.token.service.TokenService;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.application.wallet.dto.response.GetWalletInfoResponseDto;
import com.example.queue.seat_reservation.application.wallet.service.WalletService;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import com.example.queue.seat_reservation.infrastructure.wallet.dto.request.ChargeCashRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletUseCase {
    private final WalletService walletService;
    private final UserService userService;
    private final TokenService tokenService;

    public void chargeCash(String token, ChargeCashRequestDto dto) {
        String userId = tokenService.validateToken(token);
        User user = userService.getUser(userId);
        walletService.chargeCash(user, dto.getAmount());
    }

    public GetWalletInfoResponseDto getWalletInfo(String token) {
        String userId = tokenService.validateToken(token);
        User user = userService.getUser(userId);
        Wallet wallet = walletService.getWallet(user);

        return GetWalletInfoResponseDto.builder()
                .walletId(wallet.getWalletId())
                .userId(userId)
                .cash(wallet.getCash())
                .point(wallet.getPoint())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
