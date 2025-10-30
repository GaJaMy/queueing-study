package com.example.queue.seat_reservation.infrastructure.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {    // 시스템 에러
    SUCCESS(HttpStatus.OK,"SU000","ok"),
    SERVER_ERROR(HttpStatus.OK,"SY001","서버 에러"),
    ;

    private final HttpStatus status;
    private final String code;
    private final String msg;

    ErrorCode(HttpStatus status, String errorCode, String msg) {
        this.status = status;
        this.code = errorCode;
        this.msg = msg;
    }
}
