package com.example.queue.seat_reservation.application.reservation.adaptor;

import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;

import java.util.Optional;

public interface ReservationAdaptor {
    Reservation save( Reservation reservation);
    Optional<Reservation> getReservation(String reservationId);
}
