package com.example.queue.seat_reservation.application.queue.usecase;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.temporaryRepository.service.TemporaryRepositoryService;
import com.example.queue.seat_reservation.domain.token.entity.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("대기열 UseCase 테스트")
class QueueUseCaseTest {

    @Mock
    private TemporaryRepositoryService temporaryRepositoryService;

    @InjectMocks
    private QueueUseCase queueUseCase;

    @Nested
    @DisplayName("큐 토큰 발급 테스트")
    class IssueQueueTokenTest {

        @Test
        @DisplayName("성공 - 대기열 토큰 발급")
        void issueQueueToken_Success() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정 - temporaryRepositoryService의 동작 정의
             when(temporaryRepositoryService.someMethod()).thenReturn(...);

            // when
            IssueQueueResponseDto response = queueUseCase.issueQueueToken(requestDto);

            // then
            // TODO: 응답 검증 - 내부 로직 구현 후 작성
             assertThat(response).isNotNull();
             assertThat(response.getToken()).isNotNull();
             assertThat(response.getStatus()).isEqualTo("WAITING");
             assertThat(response.getQueuePosition()).isGreaterThan(0);

            // TODO: Mock 호출 검증
            // verify(temporaryRepositoryService, times(1)).someMethod();
        }

        @Test
        @DisplayName("실패 - userId가 null인 경우")
        void issueQueueToken_Fail_NullUserId() {
            // given
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(null)
                    .build();

            // when & then
            // TODO: 내부 로직 구현 후 예외 검증 작성
             assertThrows(CustomException.class, () -> queueUseCase.issueQueueToken(requestDto));
        }

        @Test
        @DisplayName("실패 - userId가 빈 문자열인 경우")
        void issueQueueToken_Fail_EmptyUserId() {
            // given
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId("")
                    .build();

            // when & then
            // TODO: 내부 로직 구현 후 예외 검증 작성
             assertThrows(CustomException.class, () -> queueUseCase.issueQueueToken(requestDto));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 사용자")
        void issueQueueToken_Fail_UserNotFound() {
            // given
            String nonExistentUserId = "nonExistentUser";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(nonExistentUserId)
                    .build();

            // TODO: Mock 설정 - 사용자 없음
             when(userService.getUser(nonExistentUserId)).thenThrow(new CustomException(ErrorCode.NOT_EXIST_USER));

            // when & then
            // TODO: 내부 로직 구현 후 예외 검증 작성
             CustomException exception = assertThrows(CustomException.class,
                     () -> queueUseCase.issueQueueToken(requestDto));
             assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
        }

        @Test
        @DisplayName("성공 - 이미 대기열에 있는 사용자는 기존 토큰 반환")
        void issueQueueToken_Success_ReturnExistingToken() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정 - 이미 존재하는 토큰
            // String existingToken = "existing-token-123";
            // when(temporaryRepositoryService.findTokenByUserId(userId)).thenReturn(Optional.of(existingToken));

            // when
            IssueQueueResponseDto response = queueUseCase.issueQueueToken(requestDto);

            // then
            // TODO: 기존 토큰 반환 검증
            // assertThat(response).isNotNull();
            // assertThat(response.getToken()).isEqualTo(existingToken);
        }

        @Test
        @DisplayName("성공 - 대기열 순서 정확히 계산됨")
        void issueQueueToken_Success_CorrectQueuePosition() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정 - 대기열 카운트
            // when(temporaryRepositoryService.getWaitingQueueCount()).thenReturn(10);

            // when
            IssueQueueResponseDto response = queueUseCase.issueQueueToken(requestDto);

            // then
            // TODO: 대기열 순서 검증
            // assertThat(response.getQueuePosition()).isEqualTo(11); // 기존 10명 + 1
        }

        @Test
        @DisplayName("성공 - 예상 대기 시간 계산됨")
        void issueQueueToken_Success_EstimatedWaitTime() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정
            // when(temporaryRepositoryService.getWaitingQueueCount()).thenReturn(10);

            // when
            IssueQueueResponseDto response = queueUseCase.issueQueueToken(requestDto);

            // then
            // TODO: 예상 대기 시간 검증 (예: 1분당 1명 처리 가정)
            // assertThat(response.getEstimatedWaitTime()).isEqualTo(10); // 10분
        }

        @Test
        @DisplayName("성공 - 만료 시간 설정됨")
        void issueQueueToken_Success_ExpiresAtSet() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            LocalDateTime beforeTest = LocalDateTime.now();

            // when
            IssueQueueResponseDto response = queueUseCase.issueQueueToken(requestDto);

            // then
            // TODO: 만료 시간 검증 (예: 30분 후)
            // assertThat(response.getExpiresAt()).isAfter(beforeTest);
            // assertThat(response.getExpiresAt()).isBefore(beforeTest.plusMinutes(31));
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

            // TODO: Mock 설정
            // when(temporaryRepositoryService.getPosition(token)).thenReturn(5);
            // when(temporaryRepositoryService.getTokenStatus(token)).thenReturn(TokenStatus.WAITING);

            // when
            GetQueuePositionResponseDto response = queueUseCase.getQueuePosition(token);

            // then
            // TODO: 응답 검증
            // assertThat(response).isNotNull();
            // assertThat(response.getToken()).isEqualTo(token);
            // assertThat(response.getStatus()).isEqualTo(TokenStatus.WAITING);
            // assertThat(response.getQueuePosition()).isEqualTo(5);

            // TODO: Mock 호출 검증
            // verify(temporaryRepositoryService, times(1)).getPosition(token);
        }

        @Test
        @DisplayName("실패 - token이 null인 경우")
        void getQueuePosition_Fail_NullToken() {
            // given
            String token = null;

            // when & then
            // TODO: 내부 로직 구현 후 예외 검증 작성
            // assertThrows(CustomException.class, () -> queueUseCase.getQueuePosition(token));
        }

        @Test
        @DisplayName("실패 - token이 빈 문자열인 경우")
        void getQueuePosition_Fail_EmptyToken() {
            // given
            String token = "";

            // when & then
            // TODO: 내부 로직 구현 후 예외 검증 작성
            // assertThrows(CustomException.class, () -> queueUseCase.getQueuePosition(token));
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 토큰")
        void getQueuePosition_Fail_TokenNotFound() {
            // given
            String nonExistentToken = "non-existent-token";

            // TODO: Mock 설정 - 토큰 없음
            // when(temporaryRepositoryService.getPosition(nonExistentToken))
            //         .thenThrow(new CustomException(ErrorCode.INVALID_TOKEN));

            // when & then
            // TODO: 내부 로직 구현 후 예외 검증 작성
            // CustomException exception = assertThrows(CustomException.class,
            //         () -> queueUseCase.getQueuePosition(nonExistentToken));
            // assertEquals(ErrorCode.INVALID_TOKEN, exception.getErrorCode());
        }

        @Test
        @DisplayName("성공 - ACTIVE 상태 토큰 조회")
        void getQueuePosition_Success_ActiveToken() {
            // given
            String activeToken = "active-token-123";

            // TODO: Mock 설정 - ACTIVE 상태
            // when(temporaryRepositoryService.getTokenStatus(activeToken)).thenReturn(TokenStatus.ACTIVE);
            // when(temporaryRepositoryService.getPosition(activeToken)).thenReturn(0);

            // when
            GetQueuePositionResponseDto response = queueUseCase.getQueuePosition(activeToken);

            // then
            // TODO: ACTIVE 상태 검증
            // assertThat(response.getStatus()).isEqualTo(TokenStatus.ACTIVE);
            // assertThat(response.getQueuePosition()).isEqualTo(0);
        }

        @Test
        @DisplayName("성공 - EXPIRED 상태 토큰 조회")
        void getQueuePosition_Success_ExpiredToken() {
            // given
            String expiredToken = "expired-token-123";

            // TODO: Mock 설정 - EXPIRED 상태
            // when(temporaryRepositoryService.getTokenStatus(expiredToken)).thenReturn(TokenStatus.EXPIRED);

            // when & then
            // TODO: EXPIRED 토큰 처리 검증
            // CustomException exception = assertThrows(CustomException.class,
            //         () -> queueUseCase.getQueuePosition(expiredToken));
            // assertEquals(ErrorCode.TOKEN_EXPIRED, exception.getErrorCode());
        }

        @Test
        @DisplayName("성공 - 남은 대기 인원 계산")
        void getQueuePosition_Success_RemainingWaitCount() {
            // given
            String token = "token-123";

            // TODO: Mock 설정
            // when(temporaryRepositoryService.getPosition(token)).thenReturn(5);
            // when(temporaryRepositoryService.getRemainingCount(token)).thenReturn(4);

            // when
            GetQueuePositionResponseDto response = queueUseCase.getQueuePosition(token);

            // then
            // TODO: 남은 대기 인원 검증
            // assertThat(response.getRemainingWaitCount()).isEqualTo(4);
        }

        @Test
        @DisplayName("성공 - 예상 대기 시간 업데이트")
        void getQueuePosition_Success_UpdatedEstimatedWaitTime() {
            // given
            String token = "token-123";

            // TODO: Mock 설정
            // when(temporaryRepositoryService.getPosition(token)).thenReturn(5);

            // when
            GetQueuePositionResponseDto response = queueUseCase.getQueuePosition(token);

            // then
            // TODO: 예상 대기 시간 검증
            // assertThat(response.getEstimatedWaitTime()).isEqualTo(5); // 5분
        }

        @Test
        @DisplayName("성공 - 만료 시간 조회")
        void getQueuePosition_Success_ExpiresAt() {
            // given
            String token = "token-123";
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(30);

            // TODO: Mock 설정
            // when(temporaryRepositoryService.getExpiresAt(token)).thenReturn(expiresAt);

            // when
            GetQueuePositionResponseDto response = queueUseCase.getQueuePosition(token);

            // then
            // TODO: 만료 시간 검증
            // assertThat(response.getExpiresAt()).isEqualTo(expiresAt);
        }

        @Test
        @DisplayName("성공 - 대기열 1번인 경우")
        void getQueuePosition_Success_FirstInQueue() {
            // given
            String token = "first-token";

            // TODO: Mock 설정
            // when(temporaryRepositoryService.getPosition(token)).thenReturn(1);

            // when
            GetQueuePositionResponseDto response = queueUseCase.getQueuePosition(token);

            // then
            // TODO: 대기열 1번 검증
            // assertThat(response.getQueuePosition()).isEqualTo(1);
            // assertThat(response.getRemainingWaitCount()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("시나리오 - 토큰 발급 후 순서 조회")
        void scenario_IssueTokenThenCheckPosition() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정

            // when - 토큰 발급
            IssueQueueResponseDto issueResponse = queueUseCase.issueQueueToken(requestDto);

            // then - 토큰 발급 검증
            // assertThat(issueResponse).isNotNull();
            // String issuedToken = issueResponse.getToken();

            // when - 순서 조회
            // GetQueuePositionResponseDto positionResponse = queueUseCase.getQueuePosition(issuedToken);

            // then - 순서 조회 검증
            // assertThat(positionResponse).isNotNull();
            // assertThat(positionResponse.getToken()).isEqualTo(issuedToken);
        }

        @Test
        @DisplayName("시나리오 - 동일 사용자 중복 토큰 발급 방지")
        void scenario_PreventDuplicateTokenForSameUser() {
            // given
            String userId = "user123";
            IssueQueueRequestDto requestDto = IssueQueueRequestDto.builder()
                    .userId(userId)
                    .build();

            // TODO: Mock 설정

            // when - 첫 번째 토큰 발급
            IssueQueueResponseDto firstResponse = queueUseCase.issueQueueToken(requestDto);

            // when - 두 번째 토큰 발급 시도
            IssueQueueResponseDto secondResponse = queueUseCase.issueQueueToken(requestDto);

            // then - 동일한 토큰 반환 검증
            // assertThat(firstResponse.getToken()).isEqualTo(secondResponse.getToken());
        }
    }
}
