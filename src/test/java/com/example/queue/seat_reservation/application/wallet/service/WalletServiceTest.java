package com.example.queue.seat_reservation.application.wallet.service;

import com.example.queue.seat_reservation.application.wallet.adaptor.WalletAdaptor;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import com.example.queue.seat_reservation.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceTest {
    @Mock
    private WalletAdaptor walletAdaptor;

    @InjectMocks
    private WalletService walletService;

    @Test
    @DisplayName("지갑 가져오기")
    void get_wallet() {
        // given
        User user = User.builder()
                .userId("testUserId")
                .name("testName")
                .email("testEmail")
                .createdAt(LocalDateTime.now())
                .build();

        Wallet wallet = Wallet.builder()
                .user(user)
                .walletId(1L)
                .cash(1000)
                .point(100)
                .updateAt(LocalDateTime.now())
                .build();

        when(walletAdaptor.getWallet(user.getUserId())).thenReturn(Optional.of(wallet));
        // when
        Wallet result = walletService.getWallet(user);

        // then
        assertNotNull(result);
        assertEquals(wallet.getWalletId(), result.getWalletId());
        assertEquals(wallet.getCash(), result.getCash());
        assertEquals(wallet.getPoint(), result.getPoint());
        assertEquals(wallet.getUpdateAt(), result.getUpdateAt());
        assertEquals(wallet.getUser().getUserId(), result.getUser().getUserId());
        verify(walletAdaptor, times(1)).getWallet(user.getUserId());
    }
}