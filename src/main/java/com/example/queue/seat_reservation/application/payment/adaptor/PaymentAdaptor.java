package com.example.queue.seat_reservation.application.payment.adaptor;

import com.example.queue.seat_reservation.domain.payment.entity.Payment;

public interface PaymentAdaptor {
    Payment save(Payment payment);
}
