package com.example.queue.seat_reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication(exclude = RedisAutoConfiguration.class)
@EnableJpaAuditing
@EnableScheduling
public class SeatReservationSystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SeatReservationSystemApplication.class, args);
	}

}
