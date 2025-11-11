package com.example.queue.seat_reservation.application.payment.service;

import com.example.queue.seat_reservation.application.payment.adaptor.PaymentAdaptor;
import com.example.queue.seat_reservation.application.wallet.service.WalletService;
import com.example.queue.seat_reservation.domain.payment.entity.Payment;
import com.example.queue.seat_reservation.domain.reservation.entity.Reservation;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentAdaptor paymentAdaptor;
    private final WalletService walletService;

    public Payment pay(User user, Reservation reservation, boolean isUsePoint) {
        Wallet wallet = user.getWallet();
        Integer price = reservation.getPrice();
        int usedPoint = 0;
        int usedCash = 0;
        int pointEarned = 0;

        if (isUsePoint) {
            usedPoint = walletService.usePoint(wallet, price);
            usedCash = walletService.useCash(wallet, price - usedPoint);
        } else {
            usedCash = wallet.consumeCash(price);
            pointEarned = usedCash * 5 / 100;
        }

        Payment payment = Payment.builder()
                .reservation(reservation)
                .user(user)
                .totalAmount(price)
                .pointUsed(usedPoint)
                .cashUsed(usedCash)
                .pointEarned(pointEarned)
                .build();

        return paymentAdaptor.save(payment);
    }
}
