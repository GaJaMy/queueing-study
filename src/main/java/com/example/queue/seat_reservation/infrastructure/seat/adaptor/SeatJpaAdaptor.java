package com.example.queue.seat_reservation.infrastructure.seat.adaptor;

import com.example.queue.seat_reservation.application.seat.adaptor.SeatAdaptor;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import com.example.queue.seat_reservation.infrastructure.seat.repository.SeatRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class SeatJpaAdaptor implements SeatAdaptor {
    private final SeatRepository seatRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Seat> getAvailableSeat() {
        return null;
    }

    @Override
    public List<Seat> getTotalSeat() {
        return seatRepository.findAll();
    }

    @Override
    public long getAvailableSeatCount() {
        return seatRepository.countByStatus(SeatStatus.AVAILABLE);
    }

    @Override
    public Optional<Seat> getSeatBySeatId(String seatId) {
        return seatRepository.findByIdWithLock(seatId);
    }

    @Override
    public void updateSeat() {

    }
}
