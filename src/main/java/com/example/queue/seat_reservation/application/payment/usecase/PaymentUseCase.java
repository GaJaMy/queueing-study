package com.example.queue.seat_reservation.application.payment.usecase;

import com.example.queue.seat_reservation.application.payment.dto.request.PayRequestDto;
import com.example.queue.seat_reservation.application.payment.dto.response.PayResponseDto;
import com.example.queue.seat_reservation.application.token.service.TokenService;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentUseCase {
    private final TokenService tokenService;
    private final UserService userService;

    public PayResponseDto pay(String token, PayRequestDto dto) {
        String userId = tokenService.validateToken(token);
        User user = userService.getUser(userId);


        return null;
    }
}
