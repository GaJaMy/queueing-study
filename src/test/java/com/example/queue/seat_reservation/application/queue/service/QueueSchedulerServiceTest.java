package com.example.queue.seat_reservation.application.queue.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QueueSchedulerService 단위 테스트")
class QueueSchedulerServiceTest {

    @Mock
    private QueueService queueService;

    @InjectMocks
    private QueueSchedulerService queueSchedulerService;

    @Nested
    @DisplayName("대기열 활성화 테스트")
    class ActivateWaitingQueueTest {

        @Test
        @DisplayName("성공 - 활성 토큰 500명 미만, 10명 활성화")
        void activateWaitingQueue_Success_LessThan500() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(450L);
            when(queueService.getWaitingTokenCount()).thenReturn(100L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then
            verify(queueService, times(1)).getActiveTokenCount();
            verify(queueService, times(1)).getWaitingTokenCount();
            verify(queueService, times(1)).refreshActiveToken();
            verify(queueService, times(1)).update10TokenActivate();
        }

        @Test
        @DisplayName("성공 - 활성 토큰 500명 이상, 활성화 안 함")
        void activateWaitingQueue_Success_500OrMore() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(500L);
            when(queueService.getWaitingTokenCount()).thenReturn(100L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then
            verify(queueService, times(1)).getActiveTokenCount();
            verify(queueService, times(1)).getWaitingTokenCount();
            verify(queueService, times(1)).refreshActiveToken();
            verify(queueService, never()).update10TokenActivate();
        }

        @Test
        @DisplayName("성공 - 활성 토큰 0명, 10명 활성화")
        void activateWaitingQueue_Success_ZeroActive() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(0L);
            when(queueService.getWaitingTokenCount()).thenReturn(50L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then
            verify(queueService, times(1)).refreshActiveToken();
            verify(queueService, times(1)).update10TokenActivate();
        }

        @Test
        @DisplayName("성공 - 대기 토큰 0명, refresh만 실행")
        void activateWaitingQueue_Success_ZeroWaiting() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(100L);
            when(queueService.getWaitingTokenCount()).thenReturn(0L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then
            verify(queueService, times(1)).refreshActiveToken();
            verify(queueService, times(1)).update10TokenActivate();
        }

        @Test
        @DisplayName("성공 - 정확히 499명일 때 활성화")
        void activateWaitingQueue_Success_Exactly499() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(499L);
            when(queueService.getWaitingTokenCount()).thenReturn(20L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then
            verify(queueService, times(1)).update10TokenActivate();
        }

        @Test
        @DisplayName("성공 - 정확히 500명일 때 활성화 안 함")
        void activateWaitingQueue_Success_Exactly500() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(500L);
            when(queueService.getWaitingTokenCount()).thenReturn(20L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then
            verify(queueService, never()).update10TokenActivate();
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("시나리오 - refresh 후 활성화 여부 재확인")
        void scenario_RefreshThenCheck() {
            // given - refresh 전에는 500명, refresh 후 450명
            when(queueService.getActiveTokenCount())
                    .thenReturn(500L);  // 첫 조회
            when(queueService.getWaitingTokenCount()).thenReturn(100L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then - 500명이므로 활성화 안 함
            verify(queueService, times(1)).refreshActiveToken();
            verify(queueService, never()).update10TokenActivate();
        }

        @Test
        @DisplayName("시나리오 - 대기열이 많고 활성이 적을 때")
        void scenario_ManyWaitingFewActive() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(50L);
            when(queueService.getWaitingTokenCount()).thenReturn(1000L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then - 10명만 활성화
            verify(queueService, times(1)).update10TokenActivate();
        }

        @Test
        @DisplayName("시나리오 - 대기열이 적고 활성이 많을 때")
        void scenario_FewWaitingManyActive() {
            // given
            when(queueService.getActiveTokenCount()).thenReturn(490L);
            when(queueService.getWaitingTokenCount()).thenReturn(5L);

            // when
            queueSchedulerService.activateWaitingQueue();

            // then - 5명만 있어도 10명 활성화 시도 (있는 만큼만 활성화됨)
            verify(queueService, times(1)).update10TokenActivate();
        }
    }
}
