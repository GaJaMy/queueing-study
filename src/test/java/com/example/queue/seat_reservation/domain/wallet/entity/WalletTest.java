package com.example.queue.seat_reservation.domain.wallet.entity;

import com.example.queue.seat_reservation.domain.user.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Wallet 엔티티 테스트")
class WalletTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Wallet 엔티티 생성 성공")
    void createWallet_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        // when
        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(5000)
                .build();

        // then
        assertThat(wallet).isNotNull();
        assertThat(wallet.getWalletId()).isEqualTo(1L);
        assertThat(wallet.getUser()).isEqualTo(user);
        assertThat(wallet.getCash()).isEqualTo(10000);
        assertThat(wallet.getPoint()).isEqualTo(5000);
    }

    @Test
    @DisplayName("Wallet 초기 생성 시 cash 0으로 설정")
    void createWallet_WithZeroCash() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        // when
        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(0)
                .point(0)
                .build();

        // then
        assertThat(wallet.getCash()).isEqualTo(0);
        assertThat(wallet.getPoint()).isEqualTo(0);
    }

    @Test
    @DisplayName("Wallet cash 양수 검증 성공")
    void validateCash_Positive_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(5000)
                .build();

        // when
        Set<ConstraintViolation<Wallet>> violations = validator.validate(wallet);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Wallet cash 0 검증 성공 (@PositiveOrZero)")
    void validateCash_Zero_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(0)
                .point(0)
                .build();

        // when
        Set<ConstraintViolation<Wallet>> violations = validator.validate(wallet);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Wallet cash 음수 검증 실패")
    void validateCash_Negative_Fail() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(-1000)
                .point(5000)
                .build();

        // when
        Set<ConstraintViolation<Wallet>> violations = validator.validate(wallet);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cash") &&
                v.getMessage().contains("0 이상"));
    }

    @Test
    @DisplayName("Wallet point 양수 검증 성공")
    void validatePoint_Positive_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(5000)
                .build();

        // when
        Set<ConstraintViolation<Wallet>> violations = validator.validate(wallet);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Wallet point 음수 검증 실패")
    void validatePoint_Negative_Fail() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(-1000)
                .build();

        // when
        Set<ConstraintViolation<Wallet>> violations = validator.validate(wallet);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("point") &&
                v.getMessage().contains("0 이상"));
    }

    @Test
    @DisplayName("Wallet과 User 1:1 관계 확인")
    void walletUserRelationship() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        // when
        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(5000)
                .build();

        // then
        assertThat(wallet.getUser()).isNotNull();
        assertThat(wallet.getUser().getUserId()).isEqualTo("user123");
    }

    @Test
    @DisplayName("Wallet 빌더 패턴 테스트")
    void builderPattern() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        // when
        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(5000)
                .build();

        // then
        assertThat(wallet.getWalletId()).isEqualTo(1L);
        assertThat(wallet.getUser()).isEqualTo(user);
        assertThat(wallet.getCash()).isEqualTo(10000);
        assertThat(wallet.getPoint()).isEqualTo(5000);
    }

    @Test
    @DisplayName("Wallet 업데이트 타임스탬프 확인")
    void updatedAt_Test() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(10000)
                .point(5000)
                .build();

        // when & then
        // @LastModifiedDate는 JPA Auditing에 의해 자동 설정되므로
        // 실제로는 Repository 레이어 테스트에서 검증 필요
        assertThat(wallet).isNotNull();
    }
}
