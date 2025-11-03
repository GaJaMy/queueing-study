package com.example.queue.seat_reservation.application.token.service;

import com.example.queue.seat_reservation.application.temporaryRepository.service.TemporaryRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TemporaryRepositoryService temporaryRepositoryService;

    public String genQueueToken() {
        return null;
    }

    public int getTokenOrder(String token) {
        return 0;
    }

    public void saveQueueToken(String token) {

    }

    public void deleteQueueToken() {
    }

    public void updateQueueToken() {
    }
}
