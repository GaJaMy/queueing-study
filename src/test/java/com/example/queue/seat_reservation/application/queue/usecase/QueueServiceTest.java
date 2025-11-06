package com.example.queue.seat_reservation.application.queue.usecase;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.queue.service.QueueService;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import com.example.queue.seat_reservation.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("대기열 service 테스트")
class QueueServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;

    @InjectMocks
    private QueueService queueService;

    @Nested
    @DisplayName("큐 토큰 발급 테스트")
    class IssueQueueTokenTest {

        @Test
        @DisplayName("성공 - 대기열 토큰 발급")
        void issueQueueToken_Success() {
            // given
            String userId = "user123";
            String token = "test-token";

            // when
            queueService.issueQueueToken(token, userId);

            // then verify
            verify(temporaryRepositoryAdaptor, times(1))
                    .save(anyString(), any());

            verify(temporaryRepositoryAdaptor, times(1))
                    .save(anyString(), anyString());  // 1번 호출됨

            verify(temporaryRepositoryAdaptor, times(1))
                    .saveZSet(anyString(), anyString());
        }
    }
}
