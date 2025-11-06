package com.example.queue.seat_reservation.application.queue.usecase;

import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.queue.service.QueueService;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QueueUseCase {
    private final QueueService queueService;
    private final TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;
    private final UserService userService;

    //큐 발급
    @Transactional
    public IssueQueueResponseDto issueQueueToken(IssueQueueRequestDto dto) {
        // 토큰 생성
        String token = UUID.randomUUID().toString();

        // 사용자 가져오기
        User user = userService.getUser(dto.getUserId());

        // 토큰 정보 생성 및 저장
        queueService.issueQueueToken(token, user.getUserId());

        return IssueQueueResponseDto.builder()
                .token(token)
                .status(QueueTokenStatus.WAITING.name())
                .build();
    }

    // 큐 대기열 순서 가져오기
    public GetQueuePositionResponseDto getQueuePosition(String token) {
        HashMap<String, Object> queueTokenInfo = queueService.getQueueTokenInfo(token);

        String userId = (String) queueTokenInfo.get("userId");
        QueueTokenStatus status = (QueueTokenStatus) queueTokenInfo.get("status");
        userService.validateUserExists(userId);

        // 활성 상태인 경우 대기열 순서 0 반환 입장하라는 응답
        if (status.equals(QueueTokenStatus.ACTIVE)) {
            return GetQueuePositionResponseDto.builder()
                    .token(token)
                    .status(QueueTokenStatus.ACTIVE)
                    .queuePosition(0)
                    .remainingWaitCount(0)
                    .estimatedWaitTime(0)
                    .build();
        } else { // 대기 상태인 경우 대기열과 순서를 계산해서 반환 아직 입장 안됨 대기 화면에 머무름
            long position = queueService.getQueuePosition(token);
            long estimatedWaitTime = position * 10;

            return GetQueuePositionResponseDto.builder()
                    .token(token)
                    .status(QueueTokenStatus.WAITING)
                    .queuePosition((int) position + 1)
                    .remainingWaitCount((int) position)
                    .estimatedWaitTime((int) estimatedWaitTime)
                    .build();
        }
    }
}
