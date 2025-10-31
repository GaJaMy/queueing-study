package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.infrastructure.exception.CustomException;
import com.example.queue.seat_reservation.infrastructure.exception.ErrorCode;
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
@DisplayName("유저 서비스 테스트")
class UserServiceTest {
    @Mock
    private UserAdaptor userAdaptor;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("유저 등록")
    void register_user() {
        // given
        User user = new User();

        // when
        userService.registerUser(user);

        // then
        verify(userAdaptor, times(1)).saveUser(user);
    }

    @Test
    @DisplayName("유저 정보 가져오기")
    void get_user() {
        // given
        String userId = "testUserId";
        User user = User.builder()
                .userId(userId)
                .name("testName")
                .email("testEmail")
                .createdAt(LocalDateTime.now())
                .build();

        when(userAdaptor.getUser(userId)).thenReturn(Optional.of(user));
        // when
        User result = userService.getUser(userId);

        // then
        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals("testName", result.getName());
        verify(userAdaptor, times(1)).getUser(userId);
    }

    @Test
    @DisplayName("유저 정보 가져오기 - 존재 하지 않는 유저")
    void get_user_not_exist() {
        // given
        String userId = "nonExistUser";
        when(userAdaptor.getUser(userId)).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> userService.getUser(userId));

        // then
        assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
        verify(userAdaptor, times(1)).getUser(userId);
    }
}