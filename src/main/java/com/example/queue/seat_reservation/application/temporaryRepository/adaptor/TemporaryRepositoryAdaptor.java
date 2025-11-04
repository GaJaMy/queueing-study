package com.example.queue.seat_reservation.application.temporaryRepository.adaptor;

import java.util.concurrent.TimeUnit;

public interface TemporaryRepositoryAdaptor {
    void save(String key, String value);

    void save(String key, String value, long ttl, TimeUnit timeUnit);

    void save(String key, Object value);

    void saveSortedSet(String key, String value);

    void delete(String key);

    void update(String key, String value);

    void update(String key, String value, long ttl, TimeUnit timeUnit);

    void getSortedSet(String key);

    int getTotalSetSize();
    int getPositionInSortedSet(String key);
}
