package com.example.queue.seat_reservation.application.payment.usecase;

import com.example.queue.seat_reservation.application.payment.dto.request.PayRequestDto;
import com.example.queue.seat_reservation.application.payment.dto.response.PayResponseDto;
import com.example.queue.seat_reservation.application.payment.service.PaymentHistoryService;
import com.example.queue.seat_reservation.application.payment.service.PaymentService;
import com.example.queue.seat_reservation.application.reservation.service.ReservationService;
import com.example.queue.seat_reservation.application.token.service.TokenService;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.application.wallet.service.WalletService;
import com.example.queue.seat_reservation.domain.payment.entity.Payment;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.reservation.entity.ReservationStatus;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentUseCase {
    private final TokenService tokenService;
    private final UserService userService;
    private final ReservationService reservationService;
    private final WalletService walletService;
    private final PaymentService paymentService;
    private final PaymentHistoryService paymentHistoryService;

    @Transactional
    public PayResponseDto pay(String token, PayRequestDto dto) {
        String userId = tokenService.validateToken(token);
        User user = userService.getUser(userId);

        // 예약과 좌석의 상태 변경
        Reservation reservation =
                reservationService.updateReservation(dto.getReservationId(), ReservationStatus.CONFIRMED);

        // 결제 처리
        Payment pay = paymentService.pay(user, reservation, dto.isUsePoint());
        paymentHistoryService.addPaymentHistory(user, reservation, pay, dto.isUsePoint());

        return PayResponseDto.builder()
                .paymentId(pay.getPaymentId())
                .reservationId(reservation.getReservationId())
                .seatId(reservation.getSeat().getSeatId())
                .seatNumber(reservation.getSeat().getSeatNumber())
                .status(SeatStatus.CONFIRMED)
                .Amount(pay.getTotalAmount())
                .payment(PayResponseDto.PaymentDetail.builder()
                        .pointUsed(pay.getPointUsed())
                        .cashUsed(pay.getCashUsed())
                        .pointEarned(pay.getPointEarned())
                        .build())
                .balance(PayResponseDto.BalanceDetail.builder()
                        .remainingCash(user.getWallet().getCash())
                        .remainingPoint(user.getWallet().getPoint())
                        .build())
                .build();
    }
}
