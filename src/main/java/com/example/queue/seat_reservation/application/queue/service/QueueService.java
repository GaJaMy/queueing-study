package com.example.queue.seat_reservation.application.queue.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.queue.adaptor.QueueTokenAdaptor;
import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueToken;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
    private final QueueTokenAdaptor queueTokenAdaptor;
    private final UserService userService;

    //큐 발급
    @Transactional
    public IssueQueueResponseDto issueQueueToken(IssueQueueRequestDto dto) {
        // 토큰 생성
        String token = UUID.randomUUID().toString();

        // 사용자 가져오기
        User user = userService.getUser(dto.getUserId());

        QueueToken queueToken = QueueToken.builder()
                .token(token)
                .userId(user.getUserId())
                .status(QueueTokenStatus.WAITING)
                .createdAt(LocalDateTime.now())
                .activatedAt(null)
                .build();

        // 사용자-토큰 매핑 키 만들기
        String userTokenMappingKey = genUserTokenMappingKey(user.getUserId());

        queueTokenAdaptor.save(queueToken);
        temporaryRepositoryAdaptor.save(userTokenMappingKey, token);
        temporaryRepositoryAdaptor.saveSortedSet(QUEUE_WAITING_KEY, token);

        return IssueQueueResponseDto.builder()
                .token(token)
                .status(QueueTokenStatus.WAITING.name())
                .build();
    }

    private String genQueueTokenKey(String token) {
        return QUEUE_TOKEN_KEY_PREFIX + token;
    }

    private String genUserTokenMappingKey(String userId) {
        return QUEUE_USER_MAPPING_KEY_PREFIX + ":" + userId;
    }

    public QueueToken getQueueToken(String token) {
        return queueTokenAdaptor.getQueueTokenById(token).orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_TOKEN));
    }
    // 큐 대기열 순서 가져오기
    public GetQueuePositionResponseDto getQueuePosition(String token) {
        QueueToken queueToken = getQueueToken(token);

        String userId = queueToken.getUserId();
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
        Long positionInSortedSet = temporaryRepositoryAdaptor.getPositionInSortedSet(key, token);
        if (positionInSortedSet == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);
        }
        return positionInSortedSet;
    }

    public int getActiveTokenCount() {
        Integer count = temporaryRepositoryAdaptor.getIntValue(QUEUE_ACTIVE_COUNT_KEY);
        if (count == null) {
            return 0;
        }
        return count;
    }

    @Transactional
    public void update10TokenActivate() {
        List<String> tokens = temporaryRepositoryAdaptor.getSetRanking(QUEUE_WAITING_KEY,0, 9);

        int activeTokenCount = getActiveTokenCount();
        log.info("activeTokenCount1 : {}", activeTokenCount);
        for (String token : tokens) {
            QueueToken queueToken = getQueueToken(token);
            String userTokenMappingKey = genUserTokenMappingKey(queueToken.getUserId());

            try {
                queueToken.modifyStatus(QueueTokenStatus.ACTIVE);
                queueToken.modifyActivatedAt(LocalDateTime.now());
                queueToken.modifyTtl(60L/*1800L*/);
                queueTokenAdaptor.save(queueToken);

                temporaryRepositoryAdaptor.save(userTokenMappingKey, token, 60L, TimeUnit.SECONDS);
                temporaryRepositoryAdaptor.deleteSortedSet(QUEUE_WAITING_KEY, token);
                activeTokenCount++;
            } catch (Exception e) {
                log.error("key : {} is error", queueToken.getToken());
            }
        }

        log.info("activeTokenCount2 : {}", activeTokenCount);
        temporaryRepositoryAdaptor.incrementIntValue(QUEUE_ACTIVE_COUNT_KEY);
    }
}
