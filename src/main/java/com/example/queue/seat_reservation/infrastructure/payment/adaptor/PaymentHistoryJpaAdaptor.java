package com.example.queue.seat_reservation.infrastructure.payment.adaptor;

import com.example.queue.seat_reservation.application.payment.adaptor.PaymentHistoryAdaptor;
import com.example.queue.seat_reservation.domain.payment.entity.PaymentHistory;
import com.example.queue.seat_reservation.infrastructure.payment.repository.PaymentHistoryRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentHistoryJpaAdaptor implements PaymentHistoryAdaptor {
    private final PaymentHistoryRepository paymentHistoryRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public PaymentHistory save(PaymentHistory paymentHistory) {
        return paymentHistoryRepository.save(paymentHistory);
    }

    @Override
    public void getPaymentHistory() {

    }
}
