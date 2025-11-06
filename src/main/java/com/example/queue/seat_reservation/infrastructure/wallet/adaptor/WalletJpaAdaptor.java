package com.example.queue.seat_reservation.infrastructure.wallet.adaptor;

import com.example.queue.seat_reservation.application.wallet.adaptor.WalletAdaptor;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import com.example.queue.seat_reservation.infrastructure.wallet.repository.WalletRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class WalletJpaAdaptor implements WalletAdaptor {
    private final WalletRepository walletRepository;
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Optional<Wallet> getWallet(String userId) {
        return walletRepository.findByUser_UserId(userId);
    }

    @Override
    public void saveWallet(Wallet wallet) {
        walletRepository.save(wallet);
    }
}
