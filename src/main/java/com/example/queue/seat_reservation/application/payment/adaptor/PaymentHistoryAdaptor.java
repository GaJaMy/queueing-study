package com.example.queue.seat_reservation.application.payment.adaptor;

import com.example.queue.seat_reservation.domain.payment.entity.PaymentHistory;

public interface PaymentHistoryAdaptor {
    PaymentHistory save(PaymentHistory paymentHistory);
    void getPaymentHistory();
}
