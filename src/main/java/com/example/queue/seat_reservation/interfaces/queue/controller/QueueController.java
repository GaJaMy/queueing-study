package com.example.queue.seat_reservation.interfaces.queue.controller;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.dto.response.GetQueuePositionResponseDto;
import com.example.queue.seat_reservation.application.queue.dto.response.IssueQueueResponseDto;
import com.example.queue.seat_reservation.application.queue.usecase.QueueUseCase;
import com.example.queue.seat_reservation.interfaces.common.response.ResponseDto;
import com.example.queue.seat_reservation.interfaces.common.util.ResponseBuilder;
import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import com.example.queue.seat_reservation.interfaces.queue.swagger.QueueControllerDocs;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVerSion.V1 + "/queue")
@RequiredArgsConstructor
@Validated
@Tag(name = "대기열 관련 API", description = "서비스 입장 대기를 관리하기 위한 API")
public class QueueController implements QueueControllerDocs {
    private final QueueUseCase queueUseCase;

    @PostMapping("/token")
    public ResponseEntity<ResponseDto<IssueQueueResponseDto>> issueToken(
            @Valid @RequestBody IssueQueueRequestDto issueQueueRequestDto
    ) {
        IssueQueueResponseDto responseDto = queueUseCase.issueQueueToken(issueQueueRequestDto);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, responseDto);
    }

    @GetMapping("/status")
    public ResponseEntity<ResponseDto<GetQueuePositionResponseDto>> getQueueStatus(
            @RequestHeader("X-Queue-Token") String token
    ) {
        GetQueuePositionResponseDto responseDto = queueUseCase.getQueuePosition(token);
        return ResponseBuilder.buildOkResponse(ErrorCode.SUCCESS, responseDto);
    }
}
