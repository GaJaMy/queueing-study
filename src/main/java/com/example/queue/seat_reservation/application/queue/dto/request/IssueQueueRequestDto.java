package com.example.queue.seat_reservation.application.queue.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueQueueRequestDto {
    @Schema(description = "유저 아이디", example = "user001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "유저 아이디는 필수 정보 입니다.")
    private String userId;
}
