package com.example.queue.seat_reservation.infrastructure.config;

import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import com.example.queue.seat_reservation.infrastructure.seat.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final SeatRepository seatRepository;

    @Override
    public void run(String... args) {
        // 기존 데이터가 있으면 초기화하지 않음
        if (seatRepository.count() > 0) {
            log.info("Seat 데이터가 이미 존재합니다. 초기화를 건너뜁니다. (현재 좌석 수: {})", seatRepository.count());
            return;
        }

        log.info("Seat 초기 데이터를 생성합니다...");

        List<Seat> seats = new ArrayList<>();

        // VIP 좌석 10개 (가격: 150,000원)
        for (int i = 1; i <= 10; i++) {
            seats.add(Seat.builder()
                    .seatId("SEAT-VIP-" + String.format("%03d", i))
                    .seatNumber("VIP-" + String.format("%03d", i))
                    .price(150000)
                    .status(SeatStatus.AVAILABLE)
                    .build());
        }

        // R석 20개 (가격: 100,000원)
        for (int i = 1; i <= 20; i++) {
            seats.add(Seat.builder()
                    .seatId("SEAT-R-" + String.format("%03d", i))
                    .seatNumber("R-" + String.format("%03d", i))
                    .price(100000)
                    .status(SeatStatus.AVAILABLE)
                    .build());
        }

        // S석 20개 (가격: 70,000원)
        for (int i = 1; i <= 20; i++) {
            seats.add(Seat.builder()
                    .seatId("SEAT-S-" + String.format("%03d", i))
                    .seatNumber("S-" + String.format("%03d", i))
                    .price(70000)
                    .status(SeatStatus.AVAILABLE)
                    .build());
        }

        seatRepository.saveAll(seats);

        log.info("Seat 초기 데이터 생성 완료! (총 {}개)", seats.size());
        log.info("- VIP석: 10개 (150,000원)");
        log.info("- R석: 20개 (100,000원)");
        log.info("- S석: 20개 (70,000원)");
    }
}
