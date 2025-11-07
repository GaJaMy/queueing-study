package com.example.queue.seat_reservation.application.seat.usecase;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.queue.service.QueueService;
import com.example.queue.seat_reservation.application.reservation.service.ReservationService;
import com.example.queue.seat_reservation.application.seat.service.SeatService;
import com.example.queue.seat_reservation.application.token.service.TokenService;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.infrastructure.seat.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("좌석 예약 동시성 통합 테스트")
class SeatUseCaseConcurrencyTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private UserService userService;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private SeatRepository seatRepository;

    private String testSeatId;

    @BeforeEach
    void setUp() {
        // 기존 데이터 정리
        seatRepository.deleteAll();

        // 테스트용 좌석 생성
        testSeatId = "SEAT-CONCURRENCY-001";
        Seat testSeat = Seat.builder()
                .seatId(testSeatId)
                .seatNumber("CONCURRENCY-001")
                .price(100000)
                .status(SeatStatus.AVAILABLE)
                .build();
        seatRepository.save(testSeat);
    }

    @Test
    @DisplayName("동시에 100명이 같은 좌석 예약 시도 - 1명만 성공해야 함")
    void concurrentReservation_OnlyOneSuccess() throws InterruptedException {
        // given
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger alreadyReservedCount = new AtomicInteger(0);
        AtomicInteger otherErrorCount = new AtomicInteger(0);

        // when - 100개 스레드가 동시에 같은 좌석 예약 시도
        for (int i = 0; i < threadCount; i++) {
            final int userId = i;
            executorService.execute(() -> {
                try {
                    // SeatService의 reserveSeatWithLock 직접 호출
                    Seat seat = seatService.reserveSeatWithLock(testSeatId);
                    successCount.incrementAndGet();
                    System.out.println("✅ 성공: thread-" + userId);
                } catch (CustomException e) {
                    String message = e.getMessage();
                    if (e.getMessage().contains("이미 예약된 좌석 입니다.")) {
                        alreadyReservedCount.incrementAndGet();
                    } else {
                        otherErrorCount.incrementAndGet();
                    }
                    System.out.println("❌ 실패: thread-" + userId + " - " + e.getErrorCode());
                } catch (Exception e) {
                    otherErrorCount.incrementAndGet();
                    System.out.println("⚠️ 예외: thread-" + userId + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("\n=== 동시성 테스트 결과 ===");
        System.out.println("성공: " + successCount.get() + "명");
        System.out.println("이미 예약됨: " + alreadyReservedCount.get() + "명");
        System.out.println("기타 오류: " + otherErrorCount.get() + "명");
        System.out.println("총: " + (successCount.get() + alreadyReservedCount.get() + otherErrorCount.get()) + "명");

        // 정확히 1명만 성공해야 함
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(alreadyReservedCount.get()).isEqualTo(99);
        assertThat(otherErrorCount.get()).isEqualTo(0);

        // DB에서 좌석 상태 확인
        Seat seat = seatRepository.findById(testSeatId).orElseThrow();
        assertThat(seat.getStatus()).isEqualTo(SeatStatus.TEMP_RESERVED);
    }

    @Test
    @DisplayName("10명이 10개 좌석 동시 예약 - 모두 성공해야 함")
    void concurrentReservation_DifferentSeats() throws InterruptedException {
        // given - 10개 좌석 생성
        for (int i = 1; i <= 10; i++) {
            Seat seat = Seat.builder()
                    .seatId("SEAT-MULTI-" + String.format("%03d", i))
                    .seatNumber("MULTI-" + String.format("%03d", i))
                    .price(100000)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seatRepository.save(seat);
        }

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 10명이 각각 다른 좌석 예약
        for (int i = 1; i <= threadCount; i++) {
            final int seatNum = i;
            executorService.execute(() -> {
                try {
                    String seatId = "SEAT-MULTI-" + String.format("%03d", seatNum);
                    seatService.reserveSeatWithLock(seatId);
                    successCount.incrementAndGet();
                    System.out.println("✅ 좌석 " + seatId + " 예약 성공");
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("❌ 실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 모두 성공
        System.out.println("\n=== 다중 좌석 테스트 결과 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("50명이 10개 좌석 경쟁 - 정확히 10명만 성공")
    void concurrentReservation_CompetitionForSeats() throws InterruptedException {
        // given - 10개 좌석 생성
        for (int i = 1; i <= 10; i++) {
            Seat seat = Seat.builder()
                    .seatId("SEAT-COMP-" + String.format("%03d", i))
                    .seatNumber("COMP-" + String.format("%03d", i))
                    .price(100000)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seatRepository.save(seat);
        }

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 50명이 10개 좌석 랜덤 예약 시도
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executorService.execute(() -> {
                try {
                    // 랜덤하게 좌석 선택 (5명씩 같은 좌석 경쟁)
                    int seatNum = (threadId % 10) + 1;
                    String seatId = "SEAT-COMP-" + String.format("%03d", seatNum);

                    seatService.reserveSeatWithLock(seatId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 정확히 10명만 성공 (10개 좌석)
        System.out.println("\n=== 경쟁 테스트 결과 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("실패: " + failCount.get());

        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failCount.get()).isEqualTo(40);

        // 모든 좌석이 예약됨
        long reservedCount = seatRepository.countByStatus(SeatStatus.TEMP_RESERVED);
        assertThat(reservedCount).isGreaterThanOrEqualTo(10);
    }

    @Test
    @DisplayName("순차 실행 vs 동시 실행 성능 비교")
    void performanceTest_SequentialVsConcurrent() throws InterruptedException {
        // given - 100개 좌석 생성
        for (int i = 1; i <= 100; i++) {
            Seat seat = Seat.builder()
                    .seatId("SEAT-PERF-" + String.format("%03d", i))
                    .seatNumber("PERF-" + String.format("%03d", i))
                    .price(100000)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seatRepository.save(seat);
        }

        // when 1 - 순차 실행 (참고용, 실제로는 실행하지 않음)
        // 생략

        // when 2 - 동시 실행
        long startTime = System.currentTimeMillis();

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 1; i <= threadCount; i++) {
            final int seatNum = i;
            executorService.execute(() -> {
                try {
                    String seatId = "SEAT-PERF-" + String.format("%03d", seatNum);
                    seatService.reserveSeatWithLock(seatId);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    // ignore
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // then
        System.out.println("\n=== 성능 테스트 결과 ===");
        System.out.println("동시 실행 시간: " + duration + "ms");
        System.out.println("성공: " + successCount.get());

        assertThat(successCount.get()).isEqualTo(100);
        assertThat(duration).isLessThan(10000); // 10초 이내
    }
}
