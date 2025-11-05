package com.example.queue.seat_reservation.infrastructure.queueToken.repository;

import com.example.queue.seat_reservation.domain.queueToken.entity.QueueToken;
import org.springframework.data.repository.CrudRepository;

public interface QueueTokenRepository extends CrudRepository<QueueToken, String> {
}
