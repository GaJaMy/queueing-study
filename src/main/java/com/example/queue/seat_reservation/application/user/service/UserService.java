package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.infrastructure.exception.CustomException;
import com.example.queue.seat_reservation.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;

    public void registerUser(User user) {
        userAdaptor.saveUser(user);
    }

    public User getUser(String userId) {
        return userAdaptor.getUser(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
    }
}
