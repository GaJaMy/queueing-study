package com.example.queue.seat_reservation.interfaces.common.util;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import org.springframework.http.ResponseEntity;

public class ResponseBuilder {
    public static <T> ResponseEntity<ResponseDto<T>> buildOkResponse(ErrorCode errorCode, T data) {
        return ResponseEntity.ok(
                ResponseDto.<T>builder()
                        .errorCode(errorCode.getCode())
                        .msg(errorCode.getMsg())
                        .data(data)
                        .build()
        );
    }
}
