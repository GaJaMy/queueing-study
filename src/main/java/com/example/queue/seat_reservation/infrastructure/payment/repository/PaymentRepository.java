package com.example.queue.seat_reservation.infrastructure.payment.repository;

import com.example.queue.seat_reservation.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
