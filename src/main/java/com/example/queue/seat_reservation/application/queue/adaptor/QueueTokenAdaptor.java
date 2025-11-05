package com.example.queue.seat_reservation.application.queue.adaptor;

import com.example.queue.seat_reservation.domain.queueToken.entity.QueueToken;

import java.util.Optional;

public interface QueueTokenAdaptor {
    void save(QueueToken queueToken);
    Optional<QueueToken> getQueueTokenById(String token);
}
