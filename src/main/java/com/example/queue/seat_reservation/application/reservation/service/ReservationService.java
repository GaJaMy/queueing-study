package com.example.queue.seat_reservation.application.reservation.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.reservation.adaptor.ReservationAdaptor;
import com.example.queue.seat_reservation.application.seat.service.SeatService;
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
    private final SeatService seatService;

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

    public Reservation getReservation(String reservationId) {
        return reservationAdaptor.getReservation(reservationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_RESERVATION));
    }

    public Reservation updateReservation(String reservationId, ReservationStatus status) {
        Reservation reservation = getReservation(reservationId);
        reservation.modifyStatus(status);
        seatService.updateSeat(reservation.getSeat());
        return reservationAdaptor.save(reservation);
    }
}
