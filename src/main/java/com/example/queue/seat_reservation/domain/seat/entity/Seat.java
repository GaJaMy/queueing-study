package com.example.queue.seat_reservation.domain.seat.entity;

import com.example.queue.seat_reservation.domain.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "seats", indexes = {
    @Index(name = "idx_seat_number", columnList = "seat_number", unique = true),
    @Index(name = "idx_seat_status", columnList = "status"),
    @Index(name = "idx_seat_number_status", columnList = "seat_number, status")
})
@EntityListeners(AuditingEntityListener.class)
public class Seat {
    @Id
    @Column(name = "seat_id", length = 10)
    private String seatId;

    @Column(name = "seat_number", length = 20, nullable = false)
    private String seatNumber;

    @Column(name = "price", nullable = false)
    @Positive(message = "좌석 가격은 양수여야 합니다.")
    private Integer price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SeatStatus status;

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
