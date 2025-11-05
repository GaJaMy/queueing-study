package com.example.queue.seat_reservation.infrastructure.secheduler;

import com.example.queue.seat_reservation.application.queue.service.QueueSchedulerService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueScheduler {
    private final QueueSchedulerService queueSchedulerService;

    @Scheduled(fixedRate = 10000)
    public void scheduleQueueActivation() {
        queueSchedulerService.activateWaitingQueue();
    }
}
