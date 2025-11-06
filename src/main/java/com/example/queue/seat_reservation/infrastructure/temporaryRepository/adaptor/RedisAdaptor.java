package com.example.queue.seat_reservation.infrastructure.temporaryRepository.adaptor;

import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisAdaptor implements TemporaryRepositoryAdaptor {
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, HashMap<String, Object> value) {
        redisTemplate.opsForHash().putAll(key, value);
    }

    @Override
    public void setHashExpire(String key, Long ttl, TimeUnit timeUnit) {
        redisTemplate.expire(key, ttl, timeUnit);
    }

    @Override
    public void save(String key, HashMap<String, Object> value, Long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForHash().putAll(key, value);
        redisTemplate.expire(key, ttl, timeUnit);
    }

    @Override
    public void save(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void save(String key, Object value, Long ttl, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, ttl, timeUnit);
    }

    @Override
    public void saveZSet(String key, String value) {
        redisTemplate.opsForZSet().add(key, value, System.currentTimeMillis());
    }

    @Override
    public void saveSet(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    @Override
    public HashMap<String, Object> getHash(String key) {
        if (redisTemplate.hasKey(key)) {
            Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);
            return entries.entrySet().stream()
                    .collect(
                            HashMap::new,
                            (m, e) -> m.put(e.getKey().toString(), e.getValue()),
                            HashMap::putAll
                    );
        } else {
            return null;
        }
    }

    @Override
    public void deleteZSet(String key, String value) {
        redisTemplate.opsForZSet().remove(key, value);
    }

    @Override
    public void deleteSet(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    @Override
    public void deleteHash(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public String get(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    @Override
    public Set<String> getSet(String key) {
        Set<Object> members = redisTemplate.opsForSet().members(key);
        if (members == null) {
            return null;
        }

        return members.stream().map(Object::toString).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getZSet(String key) {
        Set<Object> members = redisTemplate.opsForZSet().range(key, 0, -1);
        if (members == null) {
            return null;
        }

        return members.stream().map(Object::toString).collect(Collectors.toSet());
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
    public Long getPositionInSet(String key, String value) {
        return redisTemplate.opsForZSet().rank(key, value);
    }
}
