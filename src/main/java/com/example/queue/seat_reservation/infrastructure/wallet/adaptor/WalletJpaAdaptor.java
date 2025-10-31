package com.example.queue.seat_reservation.infrastructure.wallet.adaptor;

import com.example.queue.seat_reservation.application.wallet.adaptor.WalletAdaptor;
import com.example.queue.seat_reservation.infrastructure.wallet.repository.WalletRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class WalletJpaAdaptor implements WalletAdaptor {
    private final WalletRepository walletRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void getCash() {

    }

    @Override
    public void getPoint() {

    }

    @Override
    public void useCash() {

    }

    @Override
    public void usePoint() {

    }

    @Override
    public void chargeCash() {

    }

    @Override
    public void chargePoint() {

    }
}
