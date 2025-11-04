package com.example.queue.seat_reservation.infrastructure.temporaryRepository.adaptor;

import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisAdaptor implements TemporaryRepositoryAdaptor {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void save(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void save(String key, String value, long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForHash().putAll();
    }

    @Override
    public void saveSortedSet(String key, String value) {
        redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
    }

    @Override
    public void delete(String key) {

    }

    @Override
    public void update(String key, String value) {

    }

    @Override
    public void update(String key, String value, long ttl, TimeUnit timeUnit) {

    }

    @Override
    public void getSortedSet(String key) {

    }

    @Override
    public int getTotalSetSize() {
        return 0;
    }

    @Override
    public int getPositionInSortedSet(String key) {
        return 0;
    }
}
