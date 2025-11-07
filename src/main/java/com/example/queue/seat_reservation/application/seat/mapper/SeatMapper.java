package com.example.queue.seat_reservation.application.seat.mapper;

import com.example.queue.seat_reservation.application.seat.dto.response.GetSeatListResponseDto;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    GetSeatListResponseDto.SeatInfo toSeatInfo(Seat seat);
}
