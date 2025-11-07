package com.example.queue.seat_reservation.application.seat.usecase;

import com.example.queue.seat_reservation.application.reservation.service.ReservationService;
import com.example.queue.seat_reservation.application.seat.dto.request.TempReserveRequestDto;
import com.example.queue.seat_reservation.application.seat.dto.response.GetSeatListResponseDto;
import com.example.queue.seat_reservation.application.seat.dto.response.TempReserveResponseDto;
import com.example.queue.seat_reservation.application.seat.service.SeatService;
import com.example.queue.seat_reservation.application.token.service.TokenService;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatUseCase {
    private final SeatService seatService;
    private final TokenService tokenService;
    private final UserService userService;
    private final ReservationService reservationService;

    public GetSeatListResponseDto getSeatList(String token) {
        tokenService.validateToken(token);
        List<GetSeatListResponseDto.SeatInfo> seatList = seatService.getSeatList();
        int availableSeatCount = Long.valueOf(seatService.getAvailableSeatCount()).intValue();

        return GetSeatListResponseDto.builder()
                .seats(seatList)
                .totalCount(seatList.size())
                .availableCount(availableSeatCount)
                .build();
    }

    @Transactional
    public TempReserveResponseDto reserveSeat(TempReserveRequestDto dto) {
        String userId = tokenService.validateToken(dto.getUserId());
        User user = userService.getUser(userId);

        Seat seat = seatService.reserveSeatWithLock(dto.getSeatId());

        Reservation reservation = reservationService.createReservation(user, seat);

        return TempReserveResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .seatId(seat.getSeatId())
                .seatNumber(seat.getSeatNumber())
                .status(seat.getStatus())
                .userId(user.getUserId())
                .price(reservation.getPrice())
                .reservedAt(LocalDateTime.now())
                .expiresAt(reservation.getReservedAt())
                .build();
    }
}
