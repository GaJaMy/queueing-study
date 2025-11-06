package com.example.queue.seat_reservation.application.temporaryRepository.adaptor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface TemporaryRepositoryAdaptor {
    void save(String key, HashMap<String, Object> value);

    void setHashExpire(String key, Long ttl, TimeUnit timeUnit);

    void save(String key, HashMap<String, Object> value, Long ttl, TimeUnit timeUnit);

    void save(String key, Object value);

    void save(String key, Object value, Long ttl, TimeUnit timeUnit);

    void incrementIntValue(String key);

    void saveZSet(String key, String value);

    void saveSet(String key, String value);

    Integer getIntValue(String key);

    HashMap<String, Object> getHash(String key);

    void deleteZSet(String key, String value);

    void deleteSet(String key, String value);

    Set<String> getSet(String key);

    Set<String> getZSet(String key);

    void deleteHash(String key);

    List<String> getSetRanking(String key, int start, int end);

    Long getPositionInSet(String key, String value);
}
