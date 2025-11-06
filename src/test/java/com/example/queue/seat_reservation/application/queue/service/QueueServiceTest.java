package com.example.queue.seat_reservation.application.queue.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import com.example.queue.seat_reservation.domain.queueToken.entity.QueueTokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QueueService 단위 테스트")
class QueueServiceTest {

    @Mock
    private TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;

    @InjectMocks
    private QueueService queueService;

    @Nested
    @DisplayName("토큰 발급 테스트")
    class IssueQueueTokenTest {

        @Test
        @DisplayName("성공 - 새로운 토큰 발급")
        void issueQueueToken_Success_NewToken() {
            // given
            String token = "new-token";
            String userId = "user123";

            when(temporaryRepositoryAdaptor.get(anyString())).thenReturn(null);

            // when
            queueService.issueQueueToken(token, userId);

            // then
            verify(temporaryRepositoryAdaptor, times(1))
                    .save(eq("queue:token:" + token), any(HashMap.class));
            verify(temporaryRepositoryAdaptor, times(1))
                    .save(eq("queue:user:" + userId), eq(token));
            verify(temporaryRepositoryAdaptor, times(1))
                    .saveZSet(eq("queue:waiting"), eq(token));
        }

        @Test
        @DisplayName("성공 - 기존 토큰 삭제 후 새 토큰 발급")
        void issueQueueToken_Success_DeleteOldToken() {
            // given
            String newToken = "new-token";
            String oldToken = "old-token";
            String userId = "user123";

            when(temporaryRepositoryAdaptor.get("queue:user:" + userId)).thenReturn(oldToken);

            // when
            queueService.issueQueueToken(newToken, userId);

            // then - 기존 토큰 삭제 확인
            verify(temporaryRepositoryAdaptor, times(1))
                    .deleteZSet(eq("queue:waiting"), eq(oldToken));
            verify(temporaryRepositoryAdaptor, times(1))
                    .deleteHash(eq("queue:token:" + oldToken));

            // then - 새 토큰 저장 확인
            verify(temporaryRepositoryAdaptor, times(1))
                    .save(eq("queue:token:" + newToken), any(HashMap.class));
            verify(temporaryRepositoryAdaptor, times(1))
                    .save(eq("queue:user:" + userId), eq(newToken));
            verify(temporaryRepositoryAdaptor, times(1))
                    .saveZSet(eq("queue:waiting"), eq(newToken));
        }
    }

    @Nested
    @DisplayName("토큰 정보 조회 테스트")
    class GetQueueTokenInfoTest {

        @Test
        @DisplayName("성공 - 토큰 정보 조회")
        void getQueueTokenInfo_Success() {
            // given
            String token = "test-token";
            HashMap<String, Object> expectedInfo = new HashMap<>();
            expectedInfo.put("userId", "user123");
            expectedInfo.put("status", QueueTokenStatus.WAITING);
            expectedInfo.put("createdAt", LocalDateTime.now());

            when(temporaryRepositoryAdaptor.getHash("queue:token:" + token))
                    .thenReturn(expectedInfo);

            // when
            HashMap<String, Object> result = queueService.getQueueTokenInfo(token);

            // then
            assertThat(result).isNotNull();
            assertThat(result.get("userId")).isEqualTo("user123");
            assertThat(result.get("status")).isEqualTo(QueueTokenStatus.WAITING);
        }

        @Test
        @DisplayName("실패 - 토큰이 존재하지 않음")
        void getQueueTokenInfo_Fail_TokenNotFound() {
            // given
            String token = "non-existent-token";

            when(temporaryRepositoryAdaptor.getHash("queue:token:" + token))
                    .thenReturn(null);

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> queueService.getQueueTokenInfo(token));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_TOKEN);
        }
    }

    @Nested
    @DisplayName("대기열 순서 조회 테스트")
    class GetQueuePositionTest {

        @Test
        @DisplayName("성공 - 대기열 순서 조회")
        void getQueuePosition_Success() {
            // given
            String token = "test-token";
            Long expectedPosition = 5L;

            when(temporaryRepositoryAdaptor.getPositionInSet("queue:waiting", token))
                    .thenReturn(expectedPosition);

            // when
            long result = queueService.getQueuePosition(token);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("실패 - 토큰이 대기열에 없음")
        void getQueuePosition_Fail_TokenNotInQueue() {
            // given
            String token = "test-token";

            when(temporaryRepositoryAdaptor.getPositionInSet("queue:waiting", token))
                    .thenReturn(null);

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> queueService.getQueuePosition(token));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NOT_EXIST_TOKEN);
        }
    }

    @Nested
    @DisplayName("활성 토큰 수 조회 테스트")
    class GetActiveTokenCountTest {

        @Test
        @DisplayName("성공 - 활성 토큰 수 조회")
        void getActiveTokenCount_Success() {
            // given
            Set<String> activeTokens = Set.of("token1", "token2", "token3");

            when(temporaryRepositoryAdaptor.getSet("queue:active"))
                    .thenReturn(activeTokens);

            // when
            long result = queueService.getActiveTokenCount();

            // then
            assertThat(result).isEqualTo(3L);
        }

        @Test
        @DisplayName("성공 - 활성 토큰이 없는 경우 0 반환")
        void getActiveTokenCount_Success_NoActiveTokens() {
            // given
            when(temporaryRepositoryAdaptor.getSet("queue:active"))
                    .thenReturn(null);

            // when
            long result = queueService.getActiveTokenCount();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("대기 토큰 수 조회 테스트")
    class GetWaitingTokenCountTest {

        @Test
        @DisplayName("성공 - 대기 토큰 수 조회")
        void getWaitingTokenCount_Success() {
            // given
            Set<String> waitingTokens = Set.of("token1", "token2", "token3", "token4", "token5");

            when(temporaryRepositoryAdaptor.getZSet("queue:waiting"))
                    .thenReturn(waitingTokens);

            // when
            long result = queueService.getWaitingTokenCount();

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("성공 - 대기 토큰이 없는 경우 0 반환")
        void getWaitingTokenCount_Success_NoWaitingTokens() {
            // given
            when(temporaryRepositoryAdaptor.getZSet("queue:waiting"))
                    .thenReturn(null);

            // when
            long result = queueService.getWaitingTokenCount();

            // then
            assertThat(result).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("10명 활성화 테스트")
    class Update10TokenActivateTest {

        @Test
        @DisplayName("성공 - 상위 10명 활성화")
        void update10TokenActivate_Success() {
            // given
            List<String> tokens = List.of("token1", "token2", "token3");

            HashMap<String, Object> tokenInfo1 = new HashMap<>();
            tokenInfo1.put("userId", "user1");
            tokenInfo1.put("status", QueueTokenStatus.WAITING);
            tokenInfo1.put("createdAt", LocalDateTime.now());

            when(temporaryRepositoryAdaptor.getSetRanking("queue:waiting", 0, 9))
                    .thenReturn(tokens);
            when(temporaryRepositoryAdaptor.getHash(anyString()))
                    .thenReturn(tokenInfo1);

            // when
            queueService.update10TokenActivate();

            // then
            verify(temporaryRepositoryAdaptor, times(3))
                    .getHash(anyString());
            verify(temporaryRepositoryAdaptor, times(3))
                    .save(anyString(), any(HashMap.class), eq(1800L), eq(TimeUnit.SECONDS));
            verify(temporaryRepositoryAdaptor, times(3))
                    .deleteZSet(eq("queue:waiting"), anyString());
            verify(temporaryRepositoryAdaptor, times(3))
                    .saveSet(eq("queue:active"), anyString());
        }

        @Test
        @DisplayName("성공 - 대기열이 비어있는 경우")
        void update10TokenActivate_Success_EmptyQueue() {
            // given
            when(temporaryRepositoryAdaptor.getSetRanking("queue:waiting", 0, 9))
                    .thenReturn(Collections.emptyList());

            // when
            queueService.update10TokenActivate();

            // then
            verify(temporaryRepositoryAdaptor, never())
                    .getHash(anyString());
        }
    }

    @Nested
    @DisplayName("활성 토큰 새로고침 테스트")
    class RefreshActiveTokenTest {

        @Test
        @DisplayName("성공 - 만료된 토큰 제거")
        void refreshActiveToken_Success_RemoveExpiredTokens() {
            // given
            Set<String> activeTokens = new HashSet<>();
            activeTokens.add("token1");
            activeTokens.add("token2");
            activeTokens.add("token3");

            when(temporaryRepositoryAdaptor.getSet("queue:active"))
                    .thenReturn(activeTokens);

            // token1: 존재함
            // token2: 만료됨 (null)
            // token3: 존재함
            when(temporaryRepositoryAdaptor.getHash("queue:token:token1"))
                    .thenReturn(new HashMap<>());
            when(temporaryRepositoryAdaptor.getHash("queue:token:token2"))
                    .thenReturn(null);
            when(temporaryRepositoryAdaptor.getHash("queue:token:token3"))
                    .thenReturn(new HashMap<>());

            // when
            queueService.refreshActiveToken();

            // then - token2만 제거됨
            verify(temporaryRepositoryAdaptor, times(1))
                    .deleteSet(eq("queue:active"), eq("token2"));
            verify(temporaryRepositoryAdaptor, never())
                    .deleteSet(eq("queue:active"), eq("token1"));
            verify(temporaryRepositoryAdaptor, never())
                    .deleteSet(eq("queue:active"), eq("token3"));
        }

        @Test
        @DisplayName("성공 - 모든 토큰이 유효한 경우")
        void refreshActiveToken_Success_AllTokensValid() {
            // given
            Set<String> activeTokens = Set.of("token1", "token2");

            when(temporaryRepositoryAdaptor.getSet("queue:active"))
                    .thenReturn(activeTokens);
            when(temporaryRepositoryAdaptor.getHash(anyString()))
                    .thenReturn(new HashMap<>());

            // when
            queueService.refreshActiveToken();

            // then - 아무것도 제거되지 않음
            verify(temporaryRepositoryAdaptor, never())
                    .deleteSet(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("헬퍼 메서드 테스트")
    class HelperMethodsTest {

        @Test
        @DisplayName("Queue Token Info 생성 - WAITING 상태")
        void genQueueTokenInfo_Waiting() {
            // given
            String userId = "user123";
            QueueTokenStatus status = QueueTokenStatus.WAITING;

            // when
            HashMap<String, Object> result = queueService.genQueueTokenInfo(userId, status);

            // then
            assertThat(result.get("userId")).isEqualTo(userId);
            assertThat(result.get("status")).isEqualTo(QueueTokenStatus.WAITING);
            assertThat(result.get("createdAt")).isNotNull();
            assertThat(result.get("activatedAt")).isNull();
        }

        @Test
        @DisplayName("Queue Token Info 생성 - ACTIVE 상태")
        void genQueueTokenInfo_Active() {
            // given
            String userId = "user123";
            QueueTokenStatus status = QueueTokenStatus.ACTIVE;

            // when
            HashMap<String, Object> result = queueService.genQueueTokenInfo(userId, status);

            // then
            assertThat(result.get("userId")).isEqualTo(userId);
            assertThat(result.get("status")).isEqualTo(QueueTokenStatus.ACTIVE);
            assertThat(result.get("createdAt")).isNotNull();
            assertThat(result.get("activatedAt")).isNotNull();
        }

        @Test
        @DisplayName("Queue Token Key 생성")
        void genQueueTokenKey() {
            // given
            String token = "test-token";

            // when
            String result = queueService.genQueueTokenKey(token);

            // then
            assertThat(result).isEqualTo("queue:token:test-token");
        }

        @Test
        @DisplayName("User Token Mapping Key 생성")
        void genUserTokenMappingKey() {
            // given
            String userId = "user123";

            // when
            String result = queueService.genUserTokenMappingKey(userId);

            // then
            assertThat(result).isEqualTo("queue:user:user123");
        }
    }
}
