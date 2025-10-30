package com.example.queue.seat_reservation.interfaces.queue.controller;

import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVerSion.V1 + "/queue")
@RequiredArgsConstructor
@Tag(name = "대기열 관련 API", description = "서비스 입장 대기를 관리하기 위한 API")
public class QueueController {

    @PostMapping("/token")
    public void issueToken() {
    }

    @GetMapping("status")
    public void getQueueStatus() {
    }
}
