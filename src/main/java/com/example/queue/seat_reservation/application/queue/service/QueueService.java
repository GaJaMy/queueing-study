package com.example.queue.seat_reservation.application.queue.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueService {
    private final static String QUEUE_ACTIVE_KEY = "queue:active";
    private final static String QUEUE_ACTIVE_COUNT_KEY = "queue:active:count";
    private final static String QUEUE_WAITING_KEY = "queue:waiting";
    private final static String QUEUE_TOKEN_KEY_PREFIX = "queue:token:";
    private final static String QUEUE_USER_MAPPING_KEY_PREFIX = "queue:user";

    private final TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;
    private final UserService userService;

    //큐 발급
    @Transactional
    public IssueQueueResponseDto issueQueueToken(IssueQueueRequestDto dto) {
        // 토큰 생성
        String token = UUID.randomUUID().toString();

        // 사용자 가져오기
        User user = userService.getUser(dto.getUserId());

        // 대기열 토큰 키
        String queueTokenKey = genQueueTokenKey(token);
        // 대기열 토큰 정보
        HashMap<String, Object> queueTokenInfo = genQueueTokenInfo(user.getUserId(), QueueTokenStatus.WAITING);

        // 사용자-토큰 매핑 키 만들기
        String userTokenMappingKey = genUserTokenMappingKey(user.getUserId());

        temporaryRepositoryAdaptor.save(queueTokenKey, queueTokenInfo);
        temporaryRepositoryAdaptor.save(userTokenMappingKey, token);
        temporaryRepositoryAdaptor.saveZSet(QUEUE_WAITING_KEY, token);

        return IssueQueueResponseDto.builder()
                .token(token)
                .status(QueueTokenStatus.WAITING.name())
                .build();
    }

    public HashMap<String, Object> genQueueTokenInfo(String userId, QueueTokenStatus status) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("status", status);
        map.put("createdAt", LocalDateTime.now());
        map.put("activatedAt", status == QueueTokenStatus.WAITING ? null : LocalDateTime.now());
        return map;

    }

    private String genQueueTokenKey(String token) {
        return QUEUE_TOKEN_KEY_PREFIX + token;
    }

    private String genUserTokenMappingKey(String userId) {
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

    // 큐 대기열 순서 가져오기
    public GetQueuePositionResponseDto getQueuePosition(String token) {
        HashMap<String, Object> queueTokenInfo = getQueueTokenInfo(token);

        String userId = (String) queueTokenInfo.get("userId");
        userService.validateUserExists(userId);

        long position = getQueuePosition(QUEUE_WAITING_KEY, token);
        long estimatedWaitTime = position * 10;

        return GetQueuePositionResponseDto.builder()
                .token(token)
                .status(QueueTokenStatus.WAITING)
                .queuePosition((int) position + 1)
                .remainingWaitCount((int) position)
                .estimatedWaitTime((int) estimatedWaitTime)
                .build();
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

        temporaryRepositoryAdaptor.save(queueTokenKey, queTokenInfo, 60L, TimeUnit.SECONDS);
        temporaryRepositoryAdaptor.save(userTokenMappingKey, token, 60L, TimeUnit.SECONDS);
        temporaryRepositoryAdaptor.deleteZSet(QUEUE_WAITING_KEY, token);
        temporaryRepositoryAdaptor.saveSet(QUEUE_ACTIVE_KEY, token);
    }
}
