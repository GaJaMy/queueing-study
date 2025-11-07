package com.example.queue.seat_reservation.application.reservation.service;

import com.example.queue.seat_reservation.application.reservation.adaptor.ReservationAdaptor;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.reservation.entity.ReservationStatus;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationAdaptor reservationAdaptor;

    public Reservation createReservation(User user, Seat seat) {
        Reservation reservation = Reservation.builder()
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(seat.getPrice())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        return reservationAdaptor.save(reservation);
    }
}
