package com.example.queue.seat_reservation.application.token.service;

import com.example.queue.seat_reservation.application.queue.service.QueueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private QueueService queueService;

    @InjectMocks
    private TokenService tokenService;


    @Test
    @DisplayName("큐 토큰 발급 테스트")
    void getQueueToken() {
        // given
        // (임시 저장소에서 토큰을 가져오는 로직이 추가되면 여기서 mock 설정)

        // when
        String result = tokenService.genQueueToken();

        // then
        assertNotNull(result); // 현재는 null 리턴
    }

    @Test
    @DisplayName("토큰 순서 조회 테스트")
    void getTokenOrder() {
        // given
        String token = "testToken";

        // when
        int result = tokenService.getTokenOrder(token);

        // then

    }

    @Test
    @DisplayName("큐 토큰 저장 테스트")
    void saveQueueToken() {
        // given

        // when

        // then
        // (임시 저장소에 save가 호출되는지 나중에 verify)
    }

    @Test
    @DisplayName("큐 토큰 삭제 테스트")
    void deleteQueueToken() {
        // given

        // when

        // then
    }

    @Test
    @DisplayName("큐 토큰 갱신 테스트")
    void updateQueueToken() {
        // given

        // when

        // then
    }
}