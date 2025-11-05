package com.example.queue.seat_reservation.interfaces.queue.controller;

import com.example.queue.seat_reservation.application.queue.dto.request.IssueQueueRequestDto;
import com.example.queue.seat_reservation.application.queue.service.QueueService;
import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ApiVerSion.V1 + "/queue")
@RequiredArgsConstructor
@Tag(name = "대기열 관련 API", description = "서비스 입장 대기를 관리하기 위한 API")
@Validated
public class QueueController {
    private final QueueService queueService;

    @PostMapping("/token")
    public ResponseEntity<?> issueToken(
            @Valid @RequestBody IssueQueueRequestDto issueQueueRequestDto
    ) {
        return ResponseEntity.ok(queueService.issueQueueToken(issueQueueRequestDto));
    }

    @GetMapping("status")
    public void getQueueStatus() {
    }
}
