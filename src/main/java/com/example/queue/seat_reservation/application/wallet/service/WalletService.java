package com.example.queue.seat_reservation.application.wallet.service;

import com.example.queue.seat_reservation.application.wallet.adaptor.WalletAdaptor;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {
    private final WalletAdaptor walletAdaptor;

    public Wallet getWallet(User user) {
        return walletAdaptor.getWallet(user.getUserId())
                .orElseThrow(() -> new CustomException(ErrorCode.SERVER_ERROR));
    }

    public Wallet createWallet() {
        return Wallet.builder()
                .cash(0)
                .point(0)
                .build();
    }
}
