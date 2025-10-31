package com.example.queue.seat_reservation.infrastructure.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {    // 시스템 에러
    // 404 NotFound
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "US000","존재하지 않는 유저 입니다."),

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
