package com.example.queue.seat_reservation.interfaces.queue.swagger;


import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

public interface QueueControllerDocs {
    @Operation(
            summary = "토큰 발급 API",
            description = "사용자의 대기열 토큰을 발급한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "토큰 발급 성공", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<ResponseDto<IssueQueueResponseDto>> issueToken(
            @Valid @RequestBody IssueQueueRequestDto issueQueueRequestDto
    );

    @Operation(
            summary = "대기 순서 조회 API",
            description = "현재 토큰 사용자의 대기 순번을 조회한다. 현재 입장 가능 상태라면 입장 가능 응답 전송된다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "대기 순서 조회 성공", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<ResponseDto<GetQueuePositionResponseDto>> getQueueStatus(
            @Valid @RequestHeader("X-Queue-Token") String token
    );
}
