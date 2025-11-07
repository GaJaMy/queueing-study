package com.example.queue.seat_reservation.application.token.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.queue.service.QueueService;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final QueueService queueService;
    private final TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;

    public String genQueueToken() {
        return null;
    }

    public String validateToken(String token) {
        String queueTokenKey = queueService.genQueueTokenKey(token);
        HashMap<String, Object> hash = temporaryRepositoryAdaptor.getHash(queueTokenKey);

        if (hash == null) {
            throw new CustomException(ErrorCode.NOT_EXIST_TOKEN);
        }

        QueueTokenStatus status = QueueTokenStatus.valueOf(hash.get("status").toString());
        if (!status.equals(QueueTokenStatus.ACTIVE)) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }

        return (String) hash.get("userId");
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
