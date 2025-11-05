package com.example.queue.seat_reservation.application.temporaryRepository.adaptor;

import com.example.queue.seat_reservation.domain.queueToken.entity.QueueToken;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface TemporaryRepositoryAdaptor {
    void save(String key, Object value);

    void save(String key, Object value, Long ttl, TimeUnit timeUnit);

    void incrementIntValue(String key);

    void saveSortedSet(String key, String value);

    Integer getIntValue(String key);

    void deleteSortedSet(String key, String value);

    List<String> getSetRanking(String key, int start, int end);

    Long getPositionInSortedSet(String key, String value);
}
