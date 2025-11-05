package com.example.queue.seat_reservation.infrastructure.temporaryRepository.adaptor;

import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueToken;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class RedisAdaptor implements TemporaryRepositoryAdaptor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void save(String key, Object value, Long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @Override
    public void incrementIntValue(String key) {
        redisTemplate.opsForValue().increment(key);
    }

    @Override
    public void saveSortedSet(String key, String value) {
        redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
    }

    @Override
    public Integer getIntValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return null;
        }

        if (value instanceof Integer) {
            return (Integer) value;
        }
        return Integer.parseInt(value.toString());
    }

    @Override
    public String getStringValue(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    @Override
    public void deleteSortedSet(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    @Override
    public List<String> getSetRanking(String key, int start, int end) {
        Set<Object> range = redisTemplate.opsForZSet().range(key, start, end);
        if (range == null) {
            return null;
        }
        return range.stream()
                .map(Object::toString)
                .toList();
    }

    @Override
    public Long getPositionInSortedSet(String key, String value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }
}
