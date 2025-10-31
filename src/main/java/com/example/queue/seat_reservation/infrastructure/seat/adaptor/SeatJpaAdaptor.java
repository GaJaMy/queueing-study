package com.example.queue.seat_reservation.infrastructure.seat.adaptor;

import com.example.queue.seat_reservation.application.seat.adaptor.SeatAdaptor;
import com.example.queue.seat_reservation.infrastructure.seat.repository.SeatRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class SeatJpaAdaptor implements SeatAdaptor {
    private final SeatRepository seatRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public void getAvailableSeat() {

    }

    @Override
    public void getTotalSeat() {

    }

    @Override
    public void updateSeat() {

    }
}
