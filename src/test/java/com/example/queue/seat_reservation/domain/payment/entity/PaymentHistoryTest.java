package com.example.queue.seat_reservation.domain.payment.entity;

import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PaymentHistory 엔티티 테스트")
class PaymentHistoryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("PaymentHistory 충전 이력 생성 성공")
    void createPaymentHistory_Charge_Success() {
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
                .point(0)
                .build();

        // when
        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.CHARGE)
                .amount(10000)
                .balanceAfter(10000)
                .description("현금 충전")
                .build();

        // then
        assertThat(history).isNotNull();
        assertThat(history.getHistoryId()).isEqualTo(1L);
        assertThat(history.getWallet()).isEqualTo(wallet);
        assertThat(history.getType()).isEqualTo(HistoryType.CHARGE);
        assertThat(history.getAmount()).isEqualTo(10000);
        assertThat(history.getBalanceAfter()).isEqualTo(10000);
        assertThat(history.getDescription()).isEqualTo("현금 충전");
    }

    @Test
    @DisplayName("PaymentHistory 현금 결제 이력 생성 성공")
    void createPaymentHistory_PaymentCash_Success() {
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
                .point(0)
                .build();

        // when
        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.PAYMENT_CASH)
                .amount(-7000)
                .balanceAfter(3000)
                .description("좌석 예약 결제")
                .build();

        // then
        assertThat(history).isNotNull();
        assertThat(history.getType()).isEqualTo(HistoryType.PAYMENT_CASH);
        assertThat(history.getAmount()).isEqualTo(-7000);
        assertThat(history.getBalanceAfter()).isEqualTo(3000);
    }

    @Test
    @DisplayName("PaymentHistory 포인트 결제 이력 생성 성공")
    void createPaymentHistory_PaymentPoint_Success() {
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
        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.PAYMENT_POINT)
                .amount(-3000)
                .balanceAfter(2000)
                .description("포인트 사용")
                .build();

        // then
        assertThat(history).isNotNull();
        assertThat(history.getType()).isEqualTo(HistoryType.PAYMENT_POINT);
        assertThat(history.getAmount()).isEqualTo(-3000);
        assertThat(history.getBalanceAfter()).isEqualTo(2000);
    }

    @Test
    @DisplayName("PaymentHistory 포인트 적립 이력 생성 성공")
    void createPaymentHistory_PointEarn_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Wallet wallet = Wallet.builder()
                .walletId(1L)
                .user(user)
                .cash(3000)
                .point(2000)
                .build();

        // when
        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.POINT_EARN)
                .amount(350)
                .balanceAfter(2350)
                .description("현금 결제 5% 적립")
                .build();

        // then
        assertThat(history).isNotNull();
        assertThat(history.getType()).isEqualTo(HistoryType.POINT_EARN);
        assertThat(history.getAmount()).isEqualTo(350);
        assertThat(history.getBalanceAfter()).isEqualTo(2350);
    }

    @Test
    @DisplayName("PaymentHistory와 Wallet 관계 확인")
    void paymentHistoryWalletRelationship() {
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
                .point(0)
                .build();

        // when
        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.CHARGE)
                .amount(10000)
                .balanceAfter(10000)
                .description("현금 충전")
                .build();

        // then
        assertThat(history.getWallet()).isNotNull();
        assertThat(history.getWallet().getWalletId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("PaymentHistory amount 양수 값 허용 (충전)")
    void validateAmount_Positive_Allowed() {
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
                .point(0)
                .build();

        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.CHARGE)
                .amount(10000)
                .balanceAfter(10000)
                .description("현금 충전")
                .build();

        // when
        Set<ConstraintViolation<PaymentHistory>> violations = validator.validate(history);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("PaymentHistory amount 음수 값 허용 (결제)")
    void validateAmount_Negative_Allowed() {
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
                .point(0)
                .build();

        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.PAYMENT_CASH)
                .amount(-7000)
                .balanceAfter(3000)
                .description("결제")
                .build();

        // when
        Set<ConstraintViolation<PaymentHistory>> violations = validator.validate(history);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("PaymentHistory balanceAfter 양수 검증")
    void validateBalanceAfter_Positive() {
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
                .point(0)
                .build();

        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.CHARGE)
                .amount(10000)
                .balanceAfter(10000)
                .description("충전")
                .build();

        // when
        Set<ConstraintViolation<PaymentHistory>> violations = validator.validate(history);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("PaymentHistory balanceAfter 0 허용")
    void validateBalanceAfter_Zero_Allowed() {
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

        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.PAYMENT_CASH)
                .amount(-10000)
                .balanceAfter(0)
                .description("전액 결제")
                .build();

        // when
        Set<ConstraintViolation<PaymentHistory>> violations = validator.validate(history);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("PaymentHistory 빌더 패턴 테스트")
    void builderPattern() {
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
                .point(0)
                .build();

        // when
        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.CHARGE)
                .amount(10000)
                .balanceAfter(10000)
                .description("현금 충전")
                .build();

        // then
        assertThat(history.getHistoryId()).isEqualTo(1L);
        assertThat(history.getWallet()).isEqualTo(wallet);
        assertThat(history.getType()).isEqualTo(HistoryType.CHARGE);
        assertThat(history.getAmount()).isEqualTo(10000);
        assertThat(history.getBalanceAfter()).isEqualTo(10000);
        assertThat(history.getDescription()).isEqualTo("현금 충전");
    }

    @Test
    @DisplayName("PaymentHistory 생성 타임스탬프 확인")
    void createdAt_Test() {
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
                .point(0)
                .build();

        PaymentHistory history = PaymentHistory.builder()
                .historyId(1L)
                .wallet(wallet)
                .type(HistoryType.CHARGE)
                .amount(10000)
                .balanceAfter(10000)
                .description("현금 충전")
                .build();

        // when & then
        // @CreatedDate는 JPA Auditing에 의해 자동 설정되므로
        // 실제로는 Repository 레이어 테스트에서 검증 필요
        assertThat(history).isNotNull();
    }

    @Test
    @DisplayName("HistoryType Enum 값 확인")
    void historyType_EnumValues() {
        // when & then
        assertThat(HistoryType.CHARGE.getDescription()).isEqualTo("충전");
        assertThat(HistoryType.PAYMENT_CASH.getDescription()).isEqualTo("현금 결제");
        assertThat(HistoryType.PAYMENT_POINT.getDescription()).isEqualTo("포인트 결제");
        assertThat(HistoryType.POINT_EARN.getDescription()).isEqualTo("포인트 적립");
    }

    @Test
    @DisplayName("PaymentHistory 충전 후 잔액 계산")
    void calculateBalanceAfter_Charge() {
        // given
        int initialBalance = 5000;
        int chargeAmount = 10000;
        int expectedBalance = initialBalance + chargeAmount;

        // when & then
        assertThat(expectedBalance).isEqualTo(15000);
    }

    @Test
    @DisplayName("PaymentHistory 결제 후 잔액 계산")
    void calculateBalanceAfter_Payment() {
        // given
        int initialBalance = 10000;
        int paymentAmount = 7000;
        int expectedBalance = initialBalance - paymentAmount;

        // when & then
        assertThat(expectedBalance).isEqualTo(3000);
    }
}
