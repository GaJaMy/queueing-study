package com.example.queue.seat_reservation.application.wallet.service;

import com.example.queue.seat_reservation.application.wallet.adaptor.WalletAdaptor;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void chargeCash(User user, int amount) {
        Wallet wallet = user.getWallet();
        wallet.addCash(amount);
    }

    public int usePoint(Wallet wallet, int amount) {
        return wallet.consumePoint(amount);
    }

    public int useCash(Wallet wallet, int amount) {
        return wallet.consumeCash(amount);
    }

    public Wallet consumeCash(Wallet wallet, int price, boolean isPoint) {
        Integer cash = wallet.getCash();
        Integer point = wallet.getPoint();

        if (cash + point < price) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_BALANCE);
        }

        if (isPoint) {
            price = wallet.consumePoint(price);
            wallet.consumeCash(price);
        } else {
            wallet.consumeCash(price);
        }

        return wallet;
    }
}
