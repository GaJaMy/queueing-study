package com.example.queue.seat_reservation.application.seat.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import com.example.queue.seat_reservation.infrastructure.seat.repository.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DisplayName("SeatService 동시성 테스트 - 비관적 락 검증")
class SeatServiceConcurrencyTest {

    @Autowired
    private SeatService seatService;

    @Autowired
    private SeatRepository seatRepository;

    @BeforeEach
    void setUp() {
        seatRepository.deleteAll();
    }

    @Test
    @DisplayName("비관적 락 검증 - 동시에 같은 좌석 예약 시 1명만 성공")
    void pessimisticLock_OnlyOneThreadCanReserve() throws InterruptedException {
        // given
        String seatId = "SEAT-LOCK-TEST-001";
        Seat seat = Seat.builder()
                .seatId(seatId)
                .seatNumber("LOCK-001")
                .price(100000)
                .status(SeatStatus.AVAILABLE)
                .build();
        seatRepository.save(seat);

        int threadCount = 50;
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger alreadyReservedCount = new AtomicInteger(0);
        AtomicInteger otherErrorCount = new AtomicInteger(0);

        // when
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    seatService.reserveSeatWithLock(seatId);
                    successCount.incrementAndGet();
                } catch (CustomException e) {
                    if (e.getErrorCode() == ErrorCode.ALREADY_RESERVED_SEAT) {
                        alreadyReservedCount.incrementAndGet();
                    } else {
                        otherErrorCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    otherErrorCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then
        System.out.println("=== 비관적 락 테스트 결과 ===");
        System.out.println("성공: " + successCount.get());
        System.out.println("이미 예약됨: " + alreadyReservedCount.get());
        System.out.println("기타 오류: " + otherErrorCount.get());

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(alreadyReservedCount.get()).isEqualTo(49);
        assertThat(otherErrorCount.get()).isEqualTo(0);

        // DB 상태 확인
        Seat updatedSeat = seatRepository.findById(seatId).orElseThrow();
        assertThat(updatedSeat.getStatus()).isEqualTo(SeatStatus.TEMP_RESERVED);
    }

    @Test
    @DisplayName("여러 좌석 동시 예약 - 서로 다른 좌석은 간섭 없이 예약 가능")
    void multipleSeats_NoInterference() throws InterruptedException {
        // given - 5개 좌석 생성
        for (int i = 1; i <= 5; i++) {
            Seat seat = Seat.builder()
                    .seatId("SEAT-MULTI-" + i)
                    .seatNumber("MULTI-" + i)
                    .price(100000)
                    .status(SeatStatus.AVAILABLE)
                    .build();
            seatRepository.save(seat);
        }

        int threadCount = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // when - 각 스레드가 다른 좌석 예약
        for (int i = 1; i <= threadCount; i++) {
            final int seatNum = i;
            executorService.execute(() -> {
                try {
                    seatService.reserveSeatWithLock("SEAT-MULTI-" + seatNum);
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                    System.out.println("실패: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 모두 성공
        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failCount.get()).isEqualTo(0);
    }

    @Test
    @DisplayName("이미 예약된 좌석 예약 시도 - ALREADY_RESERVED_SEAT 예외 발생")
    void reserveAlreadyReservedSeat_ThrowsException() throws InterruptedException {
        // given
        String seatId = "SEAT-ALREADY-001";
        Seat seat = Seat.builder()
                .seatId(seatId)
                .seatNumber("ALREADY-001")
                .price(100000)
                .status(SeatStatus.TEMP_RESERVED) // 이미 예약된 상태
                .build();
        seatRepository.save(seat);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        AtomicInteger alreadyReservedCount = new AtomicInteger(0);

        // when - 10명이 이미 예약된 좌석 예약 시도
        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    seatService.reserveSeatWithLock(seatId);
                } catch (CustomException e) {
                    if (e.getErrorCode() == ErrorCode.ALREADY_RESERVED_SEAT) {
                        alreadyReservedCount.incrementAndGet();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // then - 모두 ALREADY_RESERVED_SEAT 예외
        assertThat(alreadyReservedCount.get()).isEqualTo(10);
    }
}
