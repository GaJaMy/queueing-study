package com.example.queue.seat_reservation.application.queue.service;

import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueSchedulerService {
    private final QueueService queueService;

    public void activateWaitingQueue() {
        long activeTokenCount = queueService.getActiveTokenCount();
        long waitingTokenCount = queueService.getWaitingTokenCount();

        log.info("대기 : {}", waitingTokenCount);
        log.info("활성 : {}", activeTokenCount);

        // 만료된 토큰 과 활성자수 먼저 정리
        queueService.refreshActiveToken();
        if (activeTokenCount < 500) {
            // 대기 상태에 있는 10명을 active 상태로 바꾸고 count 업데이트
            queueService.update10TokenActivate();
        } else {

        }
    }
}
