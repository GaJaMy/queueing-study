package com.example.queue.seat_reservation.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {

        // 기본 문서 설정
        Info info = new Info().version("0.0.1")
                .title("")
                .description(
                        "### 콘서트 API 명세서.\n" +
                                "모든 API의 Content-Type은 `application/json`입니다.\n\n" +
                                "- `key: Content-Type`, `value: application/json`\n" +
                                "추가 정보는 아래 내용을 참고하세요."
                );

        return new OpenAPI().info(info);
    }
}
