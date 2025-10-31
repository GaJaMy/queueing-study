package com.example.queue.seat_reservation.infrastructure.wallet.repository;

import com.example.queue.seat_reservation.domain.payment.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet, Long> {
}
