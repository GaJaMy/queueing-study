package com.example.queue.seat_reservation.application.user.mapper;

import com.example.queue.seat_reservation.application.user.command.UserCreateCommand;
import com.example.queue.seat_reservation.application.user.dto.SignUpUserRequestDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserCreateCommand toUserCreateCommand(SignUpUserRequestDto signUpUserRequestDto);
}
