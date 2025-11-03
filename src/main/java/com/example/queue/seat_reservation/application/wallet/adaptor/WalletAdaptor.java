package com.example.queue.seat_reservation.application.wallet.adaptor;

import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;

import java.util.Optional;

public interface WalletAdaptor {
    Optional<Wallet> getWallet(String userId);
}
