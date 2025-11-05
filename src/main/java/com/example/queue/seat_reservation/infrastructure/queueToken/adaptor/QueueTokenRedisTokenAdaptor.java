package com.example.queue.seat_reservation.infrastructure.queueToken.adaptor;

import com.example.queue.seat_reservation.application.queue.adaptor.QueueTokenAdaptor;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueToken;
import com.example.queue.seat_reservation.infrastructure.queueToken.repository.QueueTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class QueueTokenRedisTokenAdaptor implements QueueTokenAdaptor {
    private final QueueTokenRepository queueTokenRepository;

    @Override
    public void save(QueueToken queueToken) {
        queueTokenRepository.save(queueToken);
    }

    @Override
    public Optional<QueueToken> getQueueTokenById(String token) {
        return queueTokenRepository.findById(token);
    }
}
