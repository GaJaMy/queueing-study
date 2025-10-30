package com.example.queue.seat_reservation.infrastructure.user.adaptor;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.infrastructure.user.repository.UserRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserJpaAdaptor implements UserAdaptor {
    private final UserRepository userRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<User> getUser() {
        return Optional.empty();
    }

    @Override
    public void saveUser() {

    }

    @Override
    public void deleteUser() {

    }

    @Override
    public void updateUser() {

    }
}
