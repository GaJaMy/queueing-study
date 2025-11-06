package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import com.example.queue.seat_reservation.application.user.command.UserCreateCommand;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
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
@DisplayName("UserService 단위 테스트")
class UserServiceTest {
    @Mock
    private UserAdaptor userAdaptor;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("유저 등록 테스트")
    class RegisterUserTest {

        @Test
        @DisplayName("성공 - User 객체로 등록")
        void registerUser_Success_WithUser() {
            // given
            User user = User.builder()
                    .userId("user123")
                    .name("홍길동")
                    .email("test@test.com")
                    .build();

            // when
            userService.registerUser(user);

            // then
            verify(userAdaptor, times(1)).saveUser(user);
        }

        @Test
        @DisplayName("성공 - UserCreateCommand로 User 생성 및 저장")
        void registerUser_Success_WithCommand() {
            // given
            Wallet wallet = Wallet.builder()
                    .cash(0)
                    .point(0)
                    .build();

            UserCreateCommand command = UserCreateCommand.builder()
                    .userId("user123")
                    .name("홍길동")
                    .email("test@test.com")
                    .build();

            // when
            userService.registerUser(command, wallet);

            // then
            verify(userAdaptor, times(1)).saveUser(argThat(user ->
                    user.getUserId().equals("user123") &&
                            user.getName().equals("홍길동") &&
                            user.getEmail().equals("test@test.com") &&
                            user.getWallet().equals(wallet)
            ));
        }

        @Test
        @DisplayName("성공 - UserCreateCommand로 User 객체만 생성 (저장 안 함)")
        void registerUser_Success_CreateUserObject() {
            // given
            UserCreateCommand command = UserCreateCommand.builder()
                    .userId("user123")
                    .name("홍길동")
                    .email("test@test.com")
                    .build();

            Wallet wallet = Wallet.builder().build();

            // when
            userService.registerUser(command, wallet);

            // then
            verify(userAdaptor, times(1)).saveUser(any(User.class));
        }
    }

    @Nested
    @DisplayName("유저 조회 테스트")
    class GetUserTest {

        @Test
        @DisplayName("성공 - 유저 정보 조회")
        void getUser_Success() {
            // given
            String userId = "testUserId";
            User user = User.builder()
                    .userId(userId)
                    .name("testName")
                    .email("test@test.com")
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
        @DisplayName("실패 - 존재하지 않는 유저")
        void getUser_Fail_UserNotFound() {
            // given
            String userId = "nonExistUser";
            when(userAdaptor.getUser(userId)).thenReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.getUser(userId));

            assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
            verify(userAdaptor, times(1)).getUser(userId);
        }

        @Test
        @DisplayName("성공 - null userId로 조회 시 예외")
        void getUser_Fail_NullUserId() {
            // given
            String userId = null;

            // when & then
            assertThrows(Exception.class, () -> userService.getUser(userId));
        }
    }

    @Nested
    @DisplayName("유저 존재 여부 검증 테스트")
    class ValidateUserExistsTest {

        @Test
        @DisplayName("성공 - 유저가 존재함")
        void validateUserExists_Success() {
            // given
            String userId = "user123";
            User user = User.builder()
                    .userId(userId)
                    .build();

            when(userAdaptor.getUser(userId)).thenReturn(Optional.of(user));

            // when
            userService.validateUserExists(userId);

            // then
            verify(userAdaptor, times(1)).getUser(userId);
        }

        @Test
        @DisplayName("실패 - 유저가 존재하지 않음")
        void validateUserExists_Fail_UserNotFound() {
            // given
            String userId = "nonExistUser";
            when(userAdaptor.getUser(userId)).thenReturn(Optional.empty());

            // when & then
            CustomException exception = assertThrows(CustomException.class,
                    () -> userService.validateUserExists(userId));

            assertEquals(ErrorCode.NOT_EXIST_USER, exception.getErrorCode());
            verify(userAdaptor, times(1)).getUser(userId);
        }
    }

    @Nested
    @DisplayName("통합 시나리오 테스트")
    class IntegrationScenarioTest {

        @Test
        @DisplayName("시나리오 - 유저 생성 후 조회")
        void scenario_CreateAndGetUser() {
            // given
            UserCreateCommand command = UserCreateCommand.builder()
                    .userId("user123")
                    .name("홍길동")
                    .email("test@test.com")
                    .build();

            Wallet wallet = Wallet.builder()
                    .cash(0)
                    .point(0)
                    .build();

            User savedUser = User.builder()
                    .userId("user123")
                    .name("홍길동")
                    .email("test@test.com")
                    .wallet(wallet)
                    .build();

            when(userAdaptor.getUser("user123")).thenReturn(Optional.of(savedUser));

            // when - 유저 등록
            userService.registerUser(command, wallet);

            // when - 유저 조회
            User result = userService.getUser("user123");

            // then
            assertThat(result.getUserId()).isEqualTo("user123");
            assertThat(result.getName()).isEqualTo("홍길동");
            assertThat(result.getEmail()).isEqualTo("test@test.com");
        }

        @Test
        @DisplayName("시나리오 - 유저 존재 확인 후 조회")
        void scenario_ValidateAndGetUser() {
            // given
            String userId = "user123";
            User user = User.builder()
                    .userId(userId)
                    .name("홍길동")
                    .build();

            when(userAdaptor.getUser(userId)).thenReturn(Optional.of(user));

            // when - 유저 존재 확인
            userService.validateUserExists(userId);

            // when - 유저 조회
            User result = userService.getUser(userId);

            // then
            assertThat(result.getUserId()).isEqualTo(userId);
            verify(userAdaptor, times(2)).getUser(userId);
        }
    }
}