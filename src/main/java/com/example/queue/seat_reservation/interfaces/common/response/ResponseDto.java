package com.example.queue.seat_reservation.interfaces.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDto<T> {
    @Schema(description = "에러 코드", example = "SU000")
    private String errorCode;
    @Schema(description = "응답 메시지", example = "ok")
    private String msg;
    @Setter
    @Schema(description = "응답 데이터")
    private T data;
}
