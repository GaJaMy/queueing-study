package com.example.queue.seat_reservation.infrastructure.exception;

import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e) {
        return new ResponseEntity<>("",HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
