package com.example.queue.seat_reservation.application.seat.service;

import com.example.queue.seat_reservation.application.exception.CustomException;
import com.example.queue.seat_reservation.application.exception.ErrorCode;
import com.example.queue.seat_reservation.application.seat.adaptor.SeatAdaptor;
import com.example.queue.seat_reservation.application.seat.dto.response.GetSeatListResponseDto;
import com.example.queue.seat_reservation.application.seat.mapper.SeatMapper;
import com.example.queue.seat_reservation.domain.seat.entity.Seat;
import com.example.queue.seat_reservation.domain.seat.entity.SeatStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {
    private final SeatAdaptor seatAdaptor;
    private final SeatMapper seatMapper;

    public List<GetSeatListResponseDto.SeatInfo> getSeatList() {
        List<Seat> totalSeat = seatAdaptor.getTotalSeat();
        return totalSeat.stream().map(seatMapper::toSeatInfo).toList();
    }

    public long getAvailableSeatCount() {
        return seatAdaptor.getAvailableSeatCount();
    }

    @Transactional
    public Seat reserveSeatWithLock(String seatId) {
        Seat seat = seatAdaptor.getSeatBySeatId(seatId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_SEAT));

        if (seat.getStatus() != SeatStatus.AVAILABLE) {
            throw new CustomException(ErrorCode.ALREADY_RESERVED_SEAT);
        }

        seat.modifyStatus(SeatStatus.TEMP_RESERVED);

        return seat;
    }
}
