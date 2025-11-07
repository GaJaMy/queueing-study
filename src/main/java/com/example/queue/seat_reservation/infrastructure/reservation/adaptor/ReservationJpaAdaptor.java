package com.example.queue.seat_reservation.infrastructure.reservation.adaptor;

import com.example.queue.seat_reservation.application.reservation.adaptor.ReservationAdaptor;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.infrastructure.reservation.repository.ReservationRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ReservationJpaAdaptor implements ReservationAdaptor {
    private final ReservationRepository reservationRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
}
