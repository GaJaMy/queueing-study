package com.example.queue.seat_reservation.interfaces.user.swagger;


import com.example.queue.seat_reservation.application.user.dto.SignUpUserRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "유저 관련 API", description = "사용자의 관련 기능을 담당하는 API, 현재는 사용자 생성만을 담당")
public interface UserControllerDocs {
    @Operation(
            summary = "사용자 등록 API",
            description = "아이디와 이메일 비밀번호를 받아 사용자를 등록한다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "사용자 등록 성공", useReturnTypeSchema = true)
            }
    )
    ResponseEntity<?> signup(@Valid @RequestBody SignUpUserRequestDto signUpUserRequestDto);
}
