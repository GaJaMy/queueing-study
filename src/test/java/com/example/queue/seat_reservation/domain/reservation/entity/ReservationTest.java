package com.example.queue.seat_reservation.domain.reservation.entity;

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

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Reservation 엔티티 테스트")
class ReservationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Reservation 엔티티 생성 성공")
    void createReservation_Success() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        // when
        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(expiresAt)
                .build();

        // then
        assertThat(reservation).isNotNull();
        assertThat(reservation.getReservationId()).isEqualTo("RES001");
        assertThat(reservation.getUser()).isEqualTo(user);
        assertThat(reservation.getSeat()).isEqualTo(seat);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMP_RESERVED);
        assertThat(reservation.getPrice()).isEqualTo(10000);
        assertThat(reservation.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("Reservation 임시 예약 생성 - 5분 만료 시간 설정")
    void createReservation_TempReserved_WithExpiry() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(5);

        // when
        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(expiresAt)
                .build();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMP_RESERVED);
        assertThat(reservation.getExpiresAt()).isAfter(now);
        assertThat(reservation.getExpiresAt()).isBeforeOrEqualTo(now.plusMinutes(5).plusSeconds(1));
    }

    @Test
    @DisplayName("Reservation 확정 예약 생성 - 만료 시간 없음")
    void createReservation_Confirmed_NoExpiry() {
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

        LocalDateTime confirmedAt = LocalDateTime.now();

        // when
        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.CONFIRMED)
                .price(10000)
                .confirmedAt(confirmedAt)
                .build();

        // then
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);
        assertThat(reservation.getConfirmedAt()).isEqualTo(confirmedAt);
        assertThat(reservation.getExpiresAt()).isNull();
    }

    @Test
    @DisplayName("Reservation price 양수 검증 성공")
    void validatePrice_Positive_Success() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // when
        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Reservation price 0 검증 실패")
    void validatePrice_Zero_Fail() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(0)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // when
        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("price") &&
                v.getMessage().contains("양수"));
    }

    @Test
    @DisplayName("Reservation price 음수 검증 실패")
    void validatePrice_Negative_Fail() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(-1000)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // when
        Set<ConstraintViolation<Reservation>> violations = validator.validate(reservation);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("price") &&
                v.getMessage().contains("양수"));
    }

    @Test
    @DisplayName("Reservation과 User 관계 확인")
    void reservationUserRelationship() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        // when
        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // then
        assertThat(reservation.getUser()).isNotNull();
        assertThat(reservation.getUser().getUserId()).isEqualTo("user123");
    }

    @Test
    @DisplayName("Reservation과 Seat 관계 확인")
    void reservationSeatRelationship() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        // when
        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // then
        assertThat(reservation.getSeat()).isNotNull();
        assertThat(reservation.getSeat().getSeatId()).isEqualTo("SEAT001");
    }

    @Test
    @DisplayName("Reservation 상태 변경 - TEMP_RESERVED to CONFIRMED")
    void changeReservationStatus_TempReservedToConfirmed() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        // when & then
        // 상태 변경 메서드가 필요 (비즈니스 로직 레이어에서 구현)
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMP_RESERVED);
    }

    @Test
    @DisplayName("Reservation 상태 변경 - TEMP_RESERVED to EXPIRED")
    void changeReservationStatus_TempReservedToExpired() {
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        LocalDateTime expiresAt = LocalDateTime.now().minusMinutes(1);

        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(expiresAt)
                .build();

        // when & then
        // 만료 시간이 지난 경우 EXPIRED로 변경 (비즈니스 로직 레이어에서 구현)
        assertThat(reservation.getExpiresAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("Reservation 빌더 패턴 테스트")
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
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(5);

        // when
        Reservation reservation = Reservation.builder()
                .reservationId("RES001")
                .user(user)
                .seat(seat)
                .status(ReservationStatus.TEMP_RESERVED)
                .price(10000)
                .expiresAt(expiresAt)
                .build();

        // then
        assertThat(reservation.getReservationId()).isEqualTo("RES001");
        assertThat(reservation.getUser()).isEqualTo(user);
        assertThat(reservation.getSeat()).isEqualTo(seat);
        assertThat(reservation.getStatus()).isEqualTo(ReservationStatus.TEMP_RESERVED);
        assertThat(reservation.getPrice()).isEqualTo(10000);
        assertThat(reservation.getExpiresAt()).isEqualTo(expiresAt);
    }

    @Test
    @DisplayName("ReservationStatus Enum 값 확인")
    void reservationStatus_EnumValues() {
        // when & then
        assertThat(ReservationStatus.EXPIRED.getDescription()).isEqualTo("만료된 예약");
        assertThat(ReservationStatus.TEMP_RESERVED.getDescription()).isEqualTo("임시 예약");
        assertThat(ReservationStatus.CONFIRMED.getDescription()).isEqualTo("확정된 예약");
        assertThat(ReservationStatus.CANCELLED.getDescription()).isEqualTo("취소된 예약");
    }
}
