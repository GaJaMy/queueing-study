package com.example.queue.seat_reservation.application.temporaryRepository.adaptor;

public interface TemporaryRepositoryAdaptor {
    // 대기 순번 가져오기
    void getQueueNumber();

    // 대기열 저장
    void insertQueue();

    // 대기열 삭제
    void deleteQueue();

    // 토큰 저장
    void saveToken();

    // 토큰 삭제
    void deleteToken();

    // 토큰 업데이트
    void updateToken();
}
