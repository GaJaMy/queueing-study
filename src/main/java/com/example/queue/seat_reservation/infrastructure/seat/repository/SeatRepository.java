package com.example.queue.seat_reservation.infrastructure.seat.repository;

import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, String> {
}
