package com.example.queue.seat_reservation.application.queue.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final static String QUEUE_ACTIVE_KEY = "queue:active";
    private final static String QUEUE_WAITING_KEY = "queue:waiting";
    private final static String QUEUE_TOKEN_KEY_PREFIX = "queue:token:";
    private final static String QUEUE_USER_MAPPING_KEY_PREFIX = "queue:user";
    private final static long TOKEN_EXPIRE_TIME = 1800L; // 30분

    private final TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;

    public void issueQueueToken(String token, String userId) {
        // 대기열 토큰 키
        String queueTokenKey = genQueueTokenKey(token);
        // 대기열 토큰 정보
        HashMap<String, Object> queueTokenInfo = genQueueTokenInfo(userId, QueueTokenStatus.WAITING);

        // 사용자-토큰 매핑 키 만들기
        String userTokenMappingKey = genUserTokenMappingKey(userId);

        // 이전에 발급해준 토큰이 있었다면 삭제
        String beforeToken = temporaryRepositoryAdaptor.get(userTokenMappingKey);
        if (beforeToken != null) {
            String beforeQueueTokenKey = genQueueTokenKey(beforeToken);
            temporaryRepositoryAdaptor.deleteZSet(QUEUE_WAITING_KEY, beforeToken);
            temporaryRepositoryAdaptor.deleteHash(beforeQueueTokenKey);
        }

        temporaryRepositoryAdaptor.save(queueTokenKey, queueTokenInfo);
        temporaryRepositoryAdaptor.save(userTokenMappingKey, token);
        temporaryRepositoryAdaptor.saveZSet(QUEUE_WAITING_KEY, token);
    }

    public HashMap<String, Object> genQueueTokenInfo(String userId, QueueTokenStatus status) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("status", status);
        map.put("createdAt", LocalDateTime.now());
        map.put("activatedAt", status == QueueTokenStatus.WAITING ? null : LocalDateTime.now());
        return map;

    }

    public String genQueueTokenKey(String token) {
        return QUEUE_TOKEN_KEY_PREFIX + token;
    }

    public String genUserTokenMappingKey(String userId) {
        return QUEUE_USER_MAPPING_KEY_PREFIX + ":" + userId;
    }

    public HashMap<String, Object> getQueueTokenInfo(String token) {
        String queueTokenKey = genQueueTokenKey(token);
        HashMap<String, Object> hash = temporaryRepositoryAdaptor.getHash(queueTokenKey);

        if (hash == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);
        }
        return temporaryRepositoryAdaptor.getHash(queueTokenKey);
    }

    public long getQueuePosition(String key, String token) {
        Long positionInSortedSet = temporaryRepositoryAdaptor.getPositionInSet(key, token);
        if (positionInSortedSet == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);
        }
        return positionInSortedSet;
    }

    public long getActiveTokenCount() {
        Set<String> set = temporaryRepositoryAdaptor.getSet(QUEUE_ACTIVE_KEY);
        if (set == null) {
            return 0L;
        }
        return set.size();
    }

    public long getWaitingTokenCount() {
        Set<String> set = temporaryRepositoryAdaptor.getZSet(QUEUE_WAITING_KEY);
        if (set == null) {
            return 0L;
        }
        return set.size();
    }

    public long getQueuePosition(String token) {
        return getQueuePosition(QUEUE_WAITING_KEY, token);
    }

    @Transactional
    public void update10TokenActivate() {
        List<String> tokens = temporaryRepositoryAdaptor.getSetRanking(QUEUE_WAITING_KEY, 0, 9);

        for (String token : tokens) {
            try {
                updateToken(token);
            } catch (Exception e) {
                log.error("key : {} is error", token);
            }
        }
    }

    @Transactional
    public void refreshActiveToken() {
        Set<String> set = temporaryRepositoryAdaptor.getSet(QUEUE_ACTIVE_KEY);
        for (String token : set) {
            String queueTokenKey = genQueueTokenKey(token);
            if (temporaryRepositoryAdaptor.getHash(queueTokenKey) == null) {
                temporaryRepositoryAdaptor.deleteSet(QUEUE_ACTIVE_KEY, token);
            }
        }
    }

    private void updateToken(String token) {
        String queueTokenKey = genQueueTokenKey(token);

        HashMap<String, Object> queTokenInfo = temporaryRepositoryAdaptor.getHash(queueTokenKey);

        String userId = (String) queTokenInfo.get("userId");
        String userTokenMappingKey = genUserTokenMappingKey(userId);

        // 토큰 상태 활성으로 변경
        queTokenInfo.put("status", QueueTokenStatus.ACTIVE);
        queTokenInfo.put("activatedAt", LocalDateTime.now());

        temporaryRepositoryAdaptor.save(queueTokenKey, queTokenInfo, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        temporaryRepositoryAdaptor.save(userTokenMappingKey, token, TOKEN_EXPIRE_TIME, TimeUnit.SECONDS);
        temporaryRepositoryAdaptor.deleteZSet(QUEUE_WAITING_KEY, token);
        temporaryRepositoryAdaptor.saveSet(QUEUE_ACTIVE_KEY, token);
    }
}
