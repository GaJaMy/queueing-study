package com.example.queue.seat_reservation.application.user.adaptor;

import com.example.queue.seat_reservation.domain.user.entity.User;

import java.util.Optional;

public interface UserAdaptor {
    Optional<User> getUser(String userId);
    void saveUser(User user);
    void deleteUser();
    void updateUser();
}
