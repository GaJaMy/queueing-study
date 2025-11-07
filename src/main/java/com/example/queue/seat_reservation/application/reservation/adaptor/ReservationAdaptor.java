package com.example.queue.seat_reservation.application.reservation.adaptor;

import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;

public interface ReservationAdaptor {
    Reservation save( Reservation reservation);
}
