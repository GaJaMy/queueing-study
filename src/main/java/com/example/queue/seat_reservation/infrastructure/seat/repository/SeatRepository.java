package com.example.queue.seat_reservation.infrastructure.seat.repository;

import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SeatRepository extends JpaRepository<Seat, String> {
    long countByStatus(SeatStatus seatStatus);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Seat s WHERE s.seatId = :seatId")
    Optional<Seat> findByIdWithLock(String seatId);
}
