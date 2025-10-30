package com.example.queue.seat_reservation.infrastructure.user.repository;

import com.example.queue.seat_reservation.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
