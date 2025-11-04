package com.example.queue.seat_reservation.application.queue.usecase;

import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.temporaryRepository.service.TemporaryRepositoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueUseCase {
    private final TemporaryRepositoryService temporaryRepositoryService;

    // 큐 토큰 발급
    public IssueQueueResponseDto issueQueueToken(IssueQueueRequestDto dto) {
        // 현재 active 상태인 토큰이 몇개 인지 가져오기

        // active 토큰이 500개 이하이면 active 인 토큰 만들어서 redis에 저장하고, 대기 순번은 0, 남은 대기 시간 0, 만료 시간을 준다.
        String token = UUID.randomUUID().toString();
        String userId = dto.getUserId();

        temporaryRepositoryService.saveQueueToken(token, userId);
        return null;
    }

    // 큐 대기열 순서 가져오기
    public GetQueuePositionResponseDto getQueuePosition(String token) {
        return null;
    }
}
