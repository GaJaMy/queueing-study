package com.example.queue.seat_reservation.application.temporaryRepository.service;

import com.example.queue.seat_reservation.application.temporaryRepository.adaptor.TemporaryRepositoryAdaptor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TemporaryRepositoryService {
    private final TemporaryRepositoryAdaptor temporaryRepositoryAdaptor;
}
