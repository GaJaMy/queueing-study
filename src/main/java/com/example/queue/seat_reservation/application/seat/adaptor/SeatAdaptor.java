package com.example.queue.seat_reservation.application.seat.adaptor;

import com.example.queue.seat_reservation.domain.seat.entity.Seat;

import java.util.List;
import java.util.Optional;

public interface SeatAdaptor {
    List<Seat> getAvailableSeat();

    List<Seat> getTotalSeat();

    long getAvailableSeatCount();

    Optional<Seat> getSeatBySeatId(String seatId);

    void updateSeat();
}
