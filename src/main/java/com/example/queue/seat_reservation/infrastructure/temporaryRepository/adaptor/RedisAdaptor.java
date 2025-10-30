package com.example.queue.seat_reservation.infrastructure.temporaryRepository.adaptor;

import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisAdaptor implements TemporaryRepositoryAdaptor {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void getQueueNumber() {

    }

    @Override
    public void insertQueue() {

    }

    @Override
    public void deleteQueue() {

    }

    @Override
    public void saveToken() {

    }

    @Override
    public void deleteToken() {

    }

    @Override
    public void updateToken() {

    }
}
