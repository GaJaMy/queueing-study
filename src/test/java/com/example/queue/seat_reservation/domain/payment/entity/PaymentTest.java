package com.example.queue.seat_reservation.domain.payment.entity;

import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.reservation.entity.ReservationStatus;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
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

@DisplayName("Payment 엔티티 테스트")
class PaymentTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Payment 엔티티 생성 성공 - 현금만 사용")
    void createPayment_CashOnly_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        // when
        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(500)  // 5% 적립
                .build();

        // then
        assertThat(payment).isNotNull();
        assertThat(payment.getPaymentId()).isEqualTo("PAY001");
        assertThat(payment.getTotalAmount()).isEqualTo(10000);
        assertThat(payment.getPointUsed()).isEqualTo(0);
        assertThat(payment.getCashUsed()).isEqualTo(10000);
        assertThat(payment.getPointEarned()).isEqualTo(500);
    }

    @Test
    @DisplayName("Payment 엔티티 생성 성공 - 포인트 + 현금 사용")
    void createPayment_PointAndCash_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        // when
        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(3000)
                .cashUsed(7000)
                .pointEarned(350)  // 현금 7000원의 5%
                .build();

        // then
        assertThat(payment).isNotNull();
        assertThat(payment.getPointUsed()).isEqualTo(3000);
        assertThat(payment.getCashUsed()).isEqualTo(7000);
        assertThat(payment.getPointEarned()).isEqualTo(350);
        assertThat(payment.getPointUsed() + payment.getCashUsed()).isEqualTo(payment.getTotalAmount());
    }

    @Test
    @DisplayName("Payment totalAmount 양수 검증 성공")
    void validateTotalAmount_Positive_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(500)
                .build();

        // when
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Payment totalAmount 0 검증 실패")
    void validateTotalAmount_Zero_Fail() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(0)
                .pointUsed(0)
                .cashUsed(0)
                .pointEarned(0)
                .build();

        // when
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("totalAmount") &&
                v.getMessage().contains("양수"));
    }

    @Test
    @DisplayName("Payment pointUsed 0 검증 성공")
    void validatePointUsed_Zero_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(500)
                .build();

        // when
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Payment pointUsed 음수 검증 실패")
    void validatePointUsed_Negative_Fail() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(-100)
                .cashUsed(10100)
                .pointEarned(500)
                .build();

        // when
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("pointUsed") &&
                v.getMessage().contains("0 이상"));
    }

    @Test
    @DisplayName("Payment cashUsed 음수 검증 실패")
    void validateCashUsed_Negative_Fail() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(-1000)
                .pointEarned(0)
                .build();

        // when
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("cashUsed") &&
                v.getMessage().contains("0 이상"));
    }

    @Test
    @DisplayName("Payment pointEarned 음수 검증 실패")
    void validatePointEarned_Negative_Fail() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(-100)
                .build();

        // when
        Set<ConstraintViolation<Payment>> violations = validator.validate(payment);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("pointEarned") &&
                v.getMessage().contains("0 이상"));
    }

    @Test
    @DisplayName("Payment와 Reservation 1:1 관계 확인")
    void paymentReservationRelationship() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        // when
        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(500)
                .build();

        // then
        assertThat(payment.getReservation()).isNotNull();
        assertThat(payment.getReservation().getReservationId()).isEqualTo("RES001");
    }

    @Test
    @DisplayName("Payment와 User 관계 확인")
    void paymentUserRelationship() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        // when
        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(500)
                .build();

        // then
        assertThat(payment.getUser()).isNotNull();
        assertThat(payment.getUser().getUserId()).isEqualTo("user123");
    }

    @Test
    @DisplayName("Payment 현금 5% 포인트 적립 계산")
    void calculatePointEarned_5Percent() {
        // given
        int cashUsed = 10000;
        int expectedPointEarned = (int) (cashUsed * 0.05);

        // when & then
        assertThat(expectedPointEarned).isEqualTo(500);
    }

    @Test
    @DisplayName("Payment 빌더 패턴 테스트")
    void builderPattern() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.CONFIRMED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .build();

        // when
        Payment payment = Payment.builder()
                .paymentId("PAY001")
                .reservation(reservation)
                .user(user)
                .totalAmount(10000)
                .pointUsed(0)
                .cashUsed(10000)
                .pointEarned(500)
                .build();

        // then
        assertThat(payment.getPaymentId()).isEqualTo("PAY001");
        assertThat(payment.getTotalAmount()).isEqualTo(10000);
        assertThat(payment.getPointUsed()).isEqualTo(0);
        assertThat(payment.getCashUsed()).isEqualTo(10000);
        assertThat(payment.getPointEarned()).isEqualTo(500);
    }
}
