package com.example.queue.seat_reservation.application.queue.usecase;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.queue.adaptor.QueueTokenAdaptor;
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
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("대기열 service 테스트")
class QueueServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;

    @Mock
    private QueueTokenAdaptor queueTokenAdaptor;

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
            User user = User.builder()
                    .userId(userId)
                    .wallet(null)
                    .name("홍길동")
                    .email("test@test.com")
                    .createdAt(null)
                    .build();

            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정 - temporaryRepositoryService의 동작 정의
            // when
            when(userService.getUser(anyString())).thenReturn(user);
            IssueQueueResponseDto response = queueService.issueQueueToken(requestDto);

            // then
            // TODO: 응답 검증 - 내부 로직 구현 후 작성
            assertThat(response).isNotNull();
            assertThat(response.getToken()).isNotNull();
            assertThat(response.getStatus()).isEqualTo("WAITING");

            // then verify
            verify(userService, times(1))
                    .getUser(anyString());

            verify(queueTokenAdaptor, times(1))
                    .save(any());

            verify(temporaryRepositoryAdaptor, times(1))
                    .save(anyString(), anyString());  // 1번 호출됨

            verify(temporaryRepositoryAdaptor, times(1))
                    .saveSortedSet(anyString(), anyString());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void issueQueueToken_Fail_UserNotFound() {
            // given
            String nonExistentUserId = "nonExistentUser";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(nonExistentUserId)
                    .build();

            when(userService.getUser(nonExistentUserId)).thenThrow(new CustomException(ErrorCode.NOT_EXIST_USER));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> queueService.issueQueueToken(requestDto));
            assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("큐 대기열 순서 조회 테스트")
    class GetQueuePositionTest {

        @Test
        @DisplayName("성공 - 대기열 순서 조회")
        void getQueuePosition_Success() {
            // given
            String token = "valid-token-123";

            HashMap<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("userId", "user123");
            tokenInfo.put("status", QueueTokenStatus.WAITING);
            tokenInfo.put("createdAt", LocalDateTime.now());
            tokenInfo.put("activatedAt", null);

            // TODO: Mock 설정
            when(queueService.getQueuePosition(anyString(), eq(token))).thenReturn(5L);
            when(queueService.getTokenInfo(anyString())).thenReturn(tokenInfo);

            // when
            GetQueuePositionResponseDto response = queueService.getQueuePosition(token);

            // then
            // TODO: 응답 검증
            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo(token);
            assertThat(response.getStatus()).isEqualTo(QueueTokenStatus.WAITING);
            assertThat(response.getQueuePosition()).isEqualTo(6);
            assertThat(response.getRemainingWaitCount()).isEqualTo(5);
            assertThat(response.getEstimatedWaitTime()).isEqualTo(50);
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 토큰")
        void getQueuePosition_Fail_TokenNotFound() {
            // given
            String nonExistentToken = "non-existent-token-123";

            when(queueService.getTokenInfo(anyString()))
                    .thenThrow(new CustomException(ErrorCode.NOT_EXIST_TOKEN_INFO));

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> queueService.getQueuePosition(nonExistentToken));
            assertEquals(ErrorCode.NOT_EXIST_TOKEN_INFO, exception.getErrorCode());
        }

        @Test
        @DisplayName("실패 - 사용자가 존재하지 않음")
        void getQueuePosition_Fail_UserNotFound() {
            // given
            String nonExistentToken = "non-existent-token-123";

            HashMap<String, Object> tokenInfo = new HashMap<>();
            tokenInfo.put("userId", "user123");
            tokenInfo.put("status", QueueTokenStatus.WAITING);
            tokenInfo.put("createdAt", LocalDateTime.now());
            tokenInfo.put("activatedAt", null);

            when(queueService.getTokenInfo(anyString()))
                    .thenReturn(tokenInfo);
            //void 메서드는 doThrow 사용
            doThrow(new CustomException(ErrorCode.NOT_EXIST_USER))
                    .when(userService).validateUserExists(anyString());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> queueService.getQueuePosition(nonExistentToken));
            assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
        }

        @Test
        @DisplayName("성공 - 대기열 1번인 경우")
        void getQueuePosition_Success_FirstInQueue() {
            // given
            String token = "first-token";
            HashMap<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", "user123");
            userInfo.put("status", QueueTokenStatus.WAITING);
            userInfo.put("createdAt", LocalDateTime.now());
            userInfo.put("activatedAt", null);

            when(queueService.getQueuePosition(anyString(), eq(token))).thenReturn(0L);
            when(queueService.getTokenInfo(anyString())).thenReturn(userInfo);

            // when
            GetQueuePositionResponseDto response = queueService.getQueuePosition(token);

            // then
             assertThat(response.getQueuePosition()).isEqualTo(1);
             assertThat(response.getRemainingWaitCount()).isEqualTo(0);
        }
    }
}
