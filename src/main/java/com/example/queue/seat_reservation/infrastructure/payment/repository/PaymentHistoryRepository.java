package com.example.queue.seat_reservation.infrastructure.payment.repository;

import com.example.queue.seat_reservation.domain.payment.entity.PaymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentHistoryRepository extends JpaRepository<PaymentHistory, Long> {
}
