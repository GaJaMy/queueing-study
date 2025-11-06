package com.example.queue.seat_reservation.application.user.usercase;

import com.example.queue.seat_reservation.application.user.command.UserCreateCommand;
import com.example.queue.seat_reservation.application.user.dto.SignUpUserRequestDto;
import com.example.queue.seat_reservation.application.user.mapper.UserMapper;
import com.example.queue.seat_reservation.application.user.service.UserService;
import com.example.queue.seat_reservation.application.wallet.service.WalletService;
import com.example.queue.seat_reservation.domain.wallet.entity.Wallet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUseCase {
    private final UserService userService;
    private final WalletService walletService;
    private final UserMapper userMapper;

    @Transactional
    public void signUp(SignUpUserRequestDto signUpUserRequestDto) {
        UserCreateCommand userCreateCommand = userMapper.toUserCreateCommand(signUpUserRequestDto);
        Wallet wallet = walletService.createWallet();
        userService.registerUser(userCreateCommand, wallet);
    }
}
