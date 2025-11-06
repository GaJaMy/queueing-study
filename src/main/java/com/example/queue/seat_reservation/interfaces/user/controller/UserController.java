package com.example.queue.seat_reservation.interfaces.user.controller;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.user.dto.SignUpUserRequestDto;
import com.example.queue.seat_reservation.application.user.usercase.UserUseCase;
import com.example.queue.seat_reservation.interfaces.common.util.ResponseBuilder;
import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import com.example.queue.seat_reservation.interfaces.user.swagger.UserControllerDocs;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVerSion.V1 + "/users")
@RequiredArgsConstructor
@Validated
public class UserController implements UserControllerDocs {
    private final UserUseCase userUseCase;

    @PostMapping
    public ResponseEntity<?> signup(
            @Valid @RequestBody SignUpUserRequestDto signUpUserRequestDto
    ) {
        userUseCase.signUp(signUpUserRequestDto);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, null);
    }
}
