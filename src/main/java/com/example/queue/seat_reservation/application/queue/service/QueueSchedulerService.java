package com.example.queue.seat_reservation.application.queue.service;

import com.example.queue.seat_reservation.application.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QueueSchedulerService {
    private final QueueService queueService;
    private final UserService userService;

    public void activateWaitingQueue() {
        int activeTokenCount = queueService.getActiveTokenCount();

        if (activeTokenCount < 500) {
            // 대기 상태에 있는 10명을 active 상태로 바꾸고 count 업데이트
            queueService.update10TokenActivate();
        } else {

        }
    }
}
