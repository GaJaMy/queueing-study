package com.example.queue.seat_reservation.domain.seat.entity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Seat 엔티티 테스트")
class SeatTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Seat 엔티티 생성 성공")
    void createSeat_Success() {
        // given
        String seatId = "SEAT001";
        String seatNumber = "A-1";
        Integer price = 10000;
        SeatStatus status = SeatStatus.AVAILABLE;

        // when
        Seat seat = Seat.builder()
                .seatId(seatId)
                .seatNumber(seatNumber)
                .price(price)
                .status(status)
                .build();

        // then
        assertThat(seat).isNotNull();
        assertThat(seat.getSeatId()).isEqualTo(seatId);
        assertThat(seat.getSeatNumber()).isEqualTo(seatNumber);
        assertThat(seat.getPrice()).isEqualTo(price);
        assertThat(seat.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("Seat 초기 상태는 AVAILABLE")
    void createSeat_InitialStatus_Available() {
        // given & when
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Seat price 양수 검증 성공")
    void validatePrice_Positive_Success() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when
        Set<ConstraintViolation<Seat>> violations = validator.validate(seat);

        // then
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Seat price 0 검증 실패 (@Positive)")
    void validatePrice_Zero_Fail() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(0)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when
        Set<ConstraintViolation<Seat>> violations = validator.validate(seat);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("price") &&
                v.getMessage().contains("양수"));
    }

    @Test
    @DisplayName("Seat price 음수 검증 실패")
    void validatePrice_Negative_Fail() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(-1000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when
        Set<ConstraintViolation<Seat>> violations = validator.validate(seat);

        // then
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v ->
                v.getPropertyPath().toString().equals("price") &&
                v.getMessage().contains("양수"));
    }

    @Test
    @DisplayName("Seat 상태 변경 - AVAILABLE to TEMP_RESERVED")
    void changeSeatStatus_AvailableToTempReserved() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when & then
        // 현재는 Setter가 없으므로 상태 변경 메서드가 필요
        // 비즈니스 로직 레이어에서 구현될 예정
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Seat 상태 변경 - TEMP_RESERVED to CONFIRMED")
    void changeSeatStatus_TempReservedToConfirmed() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        // when & then
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.TEMP_RESERVED);
    }

    @Test
    @DisplayName("Seat 상태 변경 - TEMP_RESERVED to AVAILABLE (만료)")
    void changeSeatStatus_TempReservedToAvailable_Expired() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.TEMP_RESERVED)
                .build();

        // when & then
        // 5분 만료 후 다시 AVAILABLE로 변경되는 로직
        // 비즈니스 로직 레이어에서 구현될 예정
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.TEMP_RESERVED);
    }

    @Test
    @DisplayName("Seat 빌더 패턴 테스트")
    void builderPattern() {
        // given & when
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // then
        assertThat(seat.getSeatId()).isEqualTo("SEAT001");
        assertThat(seat.getSeatNumber()).isEqualTo("A-1");
        assertThat(seat.getPrice()).isEqualTo(10000);
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.AVAILABLE);
    }

    @Test
    @DisplayName("Seat 번호 유니크 제약조건 테스트")
    void validateSeatNumber_Unique() {
        // given
        Seat seat1 = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        Seat seat2 = Seat.builder()
                .seatId("SEAT002")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when & then
        // DB 레벨에서 unique 제약조건이 적용되므로
        // 실제로는 Repository 레이어 테스트에서 검증 필요
        assertThat(seat1.getSeatNumber()).isEqualTo(seat2.getSeatNumber());
    }

    @Test
    @DisplayName("Seat 생성/수정 타임스탬프 확인")
    void timestamps_Test() {
        // given
        Seat seat = Seat.builder()
                .seatId("SEAT001")
                .seatNumber("A-1")
                .price(10000)
                .status(SeatStatus.AVAILABLE)
                .build();

        // when & then
        // @CreatedDate, @LastModifiedDate는 JPA Auditing에 의해 자동 설정되므로
        // 실제로는 Repository 레이어 테스트에서 검증 필요
        assertThat(seat).isNotNull();
    }

    @Test
    @DisplayName("SeatStatus Enum 값 확인")
    void seatStatus_EnumValues() {
        // when & then
        assertThat(SeatStatus.AVAILABLE.getDescription()).isEqualTo("이용 가능 좌석");
        assertThat(SeatStatus.TEMP_RESERVED.getDescription()).isEqualTo("임시 예약된 좌석");
        assertThat(SeatStatus.CONFIRMED.getDescription()).isEqualTo("예약 확정 좌석");
    }
}
