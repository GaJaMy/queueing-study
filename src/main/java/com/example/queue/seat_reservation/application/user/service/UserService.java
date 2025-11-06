package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import com.example.queue.seat_reservation.application.user.command.UserCreateCommand;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;

    public void registerUser(User user) {
        userAdaptor.saveUser(user);
    }

    public void registerUser(UserCreateCommand command, Wallet wallet) {
        User user = User.builder()
                .wallet(wallet)
                .userId(command.getUserId())
                .name(command.getName())
                .email(command.getEmail())
                .build();

        userAdaptor.saveUser(user);
    }

    @Transactional(readOnly = true)
    public User getUser(String userId) {
//        String substring = UUID.randomUUID().toString().substring(0, 8);
//        return User.builder()
//                .userId(substring)
//                .build();
        return userAdaptor.getUser(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
    }

    public void validateUserExists(String userId) {
        if (userAdaptor.getUser(userId).isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
    }
}
