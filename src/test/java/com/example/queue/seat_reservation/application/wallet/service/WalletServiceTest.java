package com.example.queue.seat_reservation.application.wallet.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.wallet.adaptor.WalletAdaptor;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("WalletService 단위 테스트")
class WalletServiceTest {
    @Mock
    private WalletAdaptor walletAdaptor;

    @InjectMocks
    private WalletService walletService;

    @Nested
    @DisplayName("지갑 조회 테스트")
    class GetWalletTest {

        @Test
        @DisplayName("성공 - 유저의 지갑 조회")
        void getWallet_Success() {
            // given
            User user = User.builder()
                    .userId("testUserId")
                    .name("testName")
                    .email("testEmail")
                    .createdAt(LocalDateTime.now())
                    .build();

            Wallet wallet = Wallet.builder()
                    .walletId(1L)
                    .cash(1000)
                    .point(100)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(walletAdaptor.getWallet(user.getUserId())).thenReturn(Optional.of(wallet));

            // when
            Wallet result = walletService.getWallet(user);

            // then
            assertNotNull(result);
            assertThat(result.getWalletId()).isEqualTo(1L);
            assertThat(result.getCash()).isEqualTo(1000);
            assertThat(result.getPoint()).isEqualTo(100);
            verify(walletAdaptor, times(1)).getWallet(user.getUserId());
        }

        @Test
        @DisplayName("실패 - 지갑이 존재하지 않음 (SERVER_ERROR)")
        void getWallet_Fail_WalletNotFound() {
            // given
            User user = User.builder()
                    .userId("testUserId")
                    .name("testName")
                    .email("testEmail")
                    .build();

            when(walletAdaptor.getWallet(user.getUserId())).thenReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> walletService.getWallet(user));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.SERVER_ERROR);
            verify(walletAdaptor, times(1)).getWallet(user.getUserId());
        }

        @Test
        @DisplayName("성공 - 유저 ID로 정확하게 조회")
        void getWallet_Success_WithCorrectUserId() {
            // given
            String expectedUserId = "user123";
            User user = User.builder()
                    .userId(expectedUserId)
                    .name("홍길동")
                    .email("test@test.com")
                    .build();

            Wallet wallet = Wallet.builder()
                    .walletId(5L)
                    .cash(5000)
                    .point(500)
                    .build();

            when(walletAdaptor.getWallet(expectedUserId)).thenReturn(Optional.of(wallet));

            // when
            Wallet result = walletService.getWallet(user);

            // then
            assertThat(result).isNotNull();
            verify(walletAdaptor, times(1)).getWallet(expectedUserId);
        }
    }

    @Nested
    @DisplayName("지갑 생성 테스트")
    class CreateWalletTest {

        @Test
        @DisplayName("성공 - 새로운 지갑 생성 (초기값 0)")
        void createWallet_Success() {
            // when
            Wallet result = walletService.createWallet();

            // then
            assertNotNull(result);
            assertThat(result.getCash()).isEqualTo(0);
            assertThat(result.getPoint()).isEqualTo(0);
        }

        @Test
        @DisplayName("성공 - 생성된 지갑의 속성 확인")
        void createWallet_Success_VerifyProperties() {
            // when
            Wallet result = walletService.createWallet();

            // then
            assertThat(result.getCash()).isZero();
            assertThat(result.getPoint()).isZero();
            assertThat(result.getWalletId()).isNull(); // ID는 DB 저장 전이므로 null
        }

        @Test
        @DisplayName("성공 - 여러 개의 지갑 생성 (독립적)")
        void createWallet_Success_MultipleCreation() {
            // when
            Wallet wallet1 = walletService.createWallet();
            Wallet wallet2 = walletService.createWallet();

            // then
            assertThat(wallet1).isNotSameAs(wallet2); // 각각 독립적인 객체
            assertThat(wallet1.getCash()).isEqualTo(wallet2.getCash());
            assertThat(wallet1.getPoint()).isEqualTo(wallet2.getPoint());
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("시나리오 - 지갑 생성 후 조회")
        void scenario_CreateAndGetWallet() {
            // given - 새 지갑 생성
            Wallet newWallet = walletService.createWallet();
            assertThat(newWallet.getCash()).isZero();
            assertThat(newWallet.getPoint()).isZero();

            // given - 저장 후 조회 시나리오
            User user = User.builder()
                    .userId("user123")
                    .name("홍길동")
                    .build();

            Wallet savedWallet = Wallet.builder()
                    .walletId(1L)
                    .cash(0)
                    .point(0)
                    .build();

            when(walletAdaptor.getWallet(user.getUserId())).thenReturn(Optional.of(savedWallet));

            // when
            Wallet result = walletService.getWallet(user);

            // then
            assertThat(result.getCash()).isEqualTo(newWallet.getCash());
            assertThat(result.getPoint()).isEqualTo(newWallet.getPoint());
        }

        @Test
        @DisplayName("시나리오 - 잔액이 있는 지갑 조회")
        void scenario_GetWalletWithBalance() {
            // given
            User user = User.builder()
                    .userId("user123")
                    .build();

            Wallet walletWithBalance = Wallet.builder()
                    .walletId(10L)
                    .cash(50000)
                    .point(5000)
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(walletAdaptor.getWallet(user.getUserId())).thenReturn(Optional.of(walletWithBalance));

            // when
            Wallet result = walletService.getWallet(user);

            // then
            assertThat(result.getCash()).isEqualTo(50000);
            assertThat(result.getPoint()).isEqualTo(5000);
            assertThat(result.getWalletId()).isEqualTo(10L);
        }
    }
}