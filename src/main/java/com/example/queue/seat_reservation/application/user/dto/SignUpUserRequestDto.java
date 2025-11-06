package com.example.queue.seat_reservation.application.user.dto;

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
public class SignUpUserRequestDto {
    @Schema(description = "사용자 고유 아아디",example = "user001", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "아이디는 필수 정보입니다.")
    private String userId;

    @Schema(description = "이메일", example = "example@example.com", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String email;

    @Schema(description = "이름", example = "홍길동", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String name;
}
