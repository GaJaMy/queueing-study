package com.example.queue.seat_reservation.application.user.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class UserCreateCommand {
    private String userId;
    private String name;
    private String email;
}
