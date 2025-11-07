package com.example.queue.seat_reservation.infrastructure.reservation.repository;

import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, String> {
}
