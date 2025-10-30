package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;
}
