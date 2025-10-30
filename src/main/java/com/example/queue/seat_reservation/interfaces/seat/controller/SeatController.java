package com.example.queue.seat_reservation.interfaces.seat.controller;

import com.example.queue.seat_reservation.interfaces.common.version.ApiVerSion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiVerSion.V1 + "/seats")
public class SeatController {

    @GetMapping
    public void getSeats() {

    }

    @PostMapping("reserve")
    public void reserveSeats() {

    }

    @PostMapping("temp-reserve")
    public void tempReserveSeat() {

    }
}
