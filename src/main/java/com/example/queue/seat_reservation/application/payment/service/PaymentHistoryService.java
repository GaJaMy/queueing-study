package com.example.queue.seat_reservation.application.payment.service;

import com.example.queue.seat_reservation.application.payment.adaptor.PaymentHistoryAdaptor;
import com.example.queue.seat_reservation.domain.payment.entity.HistoryType;
import com.example.queue.seat_reservation.domain.payment.entity.Payment;
import com.example.queue.seat_reservation.domain.payment.entity.PaymentHistory;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentHistoryService {
    private final PaymentHistoryAdaptor paymentHistoryAdaptor;

    public void addPaymentHistory(User user, Reservation reservation, Payment payment, boolean isUsePoint) {
        Wallet wallet = user.getWallet();
        HistoryType historyType =  isUsePoint ? HistoryType.PAYMENT_POINT : HistoryType.PAYMENT_CASH;

        Seat seat = reservation.getSeat();
        String description = seat.getSeatId() + "좌석 구매";

        PaymentHistory paymentHistory = PaymentHistory.builder()
                .wallet(wallet)
                .type(historyType)
                .amount(payment.getTotalAmount())
                .cashAfter(wallet.getCash())
                .pointAfter(wallet.getPoint())
                .description(description)
                .build();

        paymentHistoryAdaptor.save(paymentHistory);
    }
}
