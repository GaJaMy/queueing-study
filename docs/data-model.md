# 🎯 콘서트 예약 시스템 — 데이터 모델 설계 (Redis 토큰 반영)

## 1. Entity Relationship Diagram (ERD)

User 1 ─── Seat *

- User 1 : Seat * — 결제 완료 또는 임시 예약 좌석 관리
- Token : Redis에서 관리, Seat 임시 예약과 연동

## 2. 엔티티 정의

### 2.1 User
설명: 콘서트 예약 시스템 사용자 정보

속성:
- user_id : BIGINT, PK, AUTO_INCREMENT, 사용자 고유 ID
- name : VARCHAR(100), NOT NULL, 사용자 이름
- email : VARCHAR(255), UNIQUE, NOT NULL, 이메일
- created_at : TIMESTAMP, DEFAULT CURRENT_TIMESTAMP, 가입일

인덱스:
- PRIMARY KEY: user_id
- UNIQUE INDEX: email

### 2.2 Seat
설명: 콘서트 좌석 상태

속성:
- seat_id : VARCHAR(10), PK, 좌석 번호 (예: A1, B2)
- status : ENUM('AVAILABLE','TEMP_RESERVED','RESERVED'), NOT NULL, DEFAULT 'AVAILABLE', 좌석 상태
- reserved_by : BIGINT, FK → User(user_id), NULLABLE, 결제 완료 사용자
- temp_reserved_by : BIGINT, FK → User(user_id), NULLABLE, 임시 예약 사용자
- created_at : TIMESTAMP, DEFAULT CURRENT_TIMESTAMP, 생성일
- updated_at : TIMESTAMP, DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, 최종 수정일

인덱스:
- PRIMARY KEY: seat_id
- INDEX: status
- INDEX: reserved_by
- INDEX: temp_reserved_by

제약조건:
- status = TEMP_RESERVED → temp_reserved_by NOT NULL, reserved_by NULL
- status = RESERVED → reserved_by NOT NULL, temp_reserved_by NULL
- AVAILABLE 상태일 때 reserved_by, temp_reserved_by NULL
- token_id는 임시 예약 좌석과 Redis 토큰 연동 용도

### 2.3 TokenHistory
설명: 과거 발급된 토큰 기록 보관 (Redis 토큰과 연관은 없음)

속성:
- token_id : VARCHAR(64), PK, 발급된 토큰 값
- issued_at : TIMESTAMP, NOT NULL, 발급 시간
- expired_at : TIMESTAMP, NOT NULL, 만료 시간

인덱스:
- PRIMARY KEY: token_id
- INDEX: user_id
- INDEX: issued_at
- INDEX: expired_at

제약조건:
- token_id 고유
- expired_at 이후 재발급된 토큰과 충돌 확인 가능
- Seat나 User와 직접적인 FK 관계 없음

## 3. 관계 요약

- User ↔ Seat : 결제 완료(reserved_by), 임시 예약(temp_reserved_by) 관계
- Token : Redis에서 TTL 기반 관리, Seat.temp_reserved_by와 token_id로 연결

## 4. 추가 고려사항

- Redis에서 Token TTL 관리 → 임시 예약 만료 시 Seat.status 자동 AVAILABLE로 변경
- 대기열 순서(queue_position)와 입장 시간(entered_at) 역시 Redis에서 관리
- Seat 임시 예약 충돌 방지를 위해 status + temp_reserved_by + token_id 조합 고유 인덱스 가능