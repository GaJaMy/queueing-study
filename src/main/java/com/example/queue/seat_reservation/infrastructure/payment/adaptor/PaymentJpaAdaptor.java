package com.example.queue.seat_reservation.infrastructure.payment.adaptor;

import com.example.queue.seat_reservation.application.payment.adaptor.PaymentAdaptor;
import com.example.queue.seat_reservation.domain.payment.entity.Payment;
import com.example.queue.seat_reservation.infrastructure.payment.repository.PaymentRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentJpaAdaptor implements PaymentAdaptor {
    private final PaymentRepository paymentRepository;
    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }
}
