# 데이터 모델 설계

## 목차
1. [Entity Relationship Diagram](#1-entity-relationship-diagram-erd)
2. [엔티티 상세 정의](#2-엔티티-상세-정의)
3. [관계 정의](#3-관계-정의)
4. [인덱스 전략](#4-인덱스-전략)
5. [제약 조건](#5-제약-조건)
6. [Redis 데이터 구조](#6-redis-데이터-구조)

---

## 1. Entity Relationship Diagram (ERD)

```
┌──────────────┐           ┌──────────────┐
│     User     │           │    Wallet    │
│──────────────│           │──────────────│
│ userId (PK)  │───────────│ walletId (PK)│
│ name         │    1:1    │ userId (FK)  │
│ email        │           │ cash         │
│ createdAt    │           │ point        │
└──────────────┘           │ updatedAt    │
       │                   └──────────────┘
       │                          │
       │ 1:N                      │ 1:N
       │                          │
       ▼                          ▼
┌──────────────┐           ┌──────────────────┐
│ Reservation  │           │ PaymentHistory   │
│──────────────│           │──────────────────│
│ reservationId│◄──┐       │ historyId (PK)   │
│ userId (FK)  │   │       │ walletId (FK)    │
│ seatId (FK)  │   │ 1:1   │ type             │
│ status       │   │       │ amount           │
│ price        │   │       │ balanceAfter     │
│ reservedAt   │   │       │ description      │
│ expiresAt    │   │       │ createdAt        │
└──────────────┘   │       └──────────────────┘
       │           │
       │ N:1       │
       │           │
       ▼           │
┌──────────────┐   │
│     Seat     │   │
│──────────────│   │
│ seatId (PK)  │   │
│ seatNumber   │   │
│ price        │   │
│ status       │   │
│ createdAt    │   │
│ updatedAt    │   │
└──────────────┘   │
                   │
                   │
            ┌──────┴──────┐
            │   Payment   │
            │─────────────│
            │ paymentId   │
            │ reservationId│
            │ userId (FK) │
            │ totalAmount │
            │ pointUsed   │
            │ cashUsed    │
            │ pointEarned │
            │ paidAt      │
            └─────────────┘

┌─────────────────────────┐
│  TokenHistory (RDB)     │
│─────────────────────────│
│ token (PK)              │
│ userId (FK)             │
│ status                  │
│ createdAt               │
│ activatedAt             │
│ expiredAt               │
└─────────────────────────┘

┌─────────────────────────┐
│  QueueToken (Redis)     │
│─────────────────────────│
│ token (Key)             │
│ userId                  │
│ status (WAITING/ACTIVE) │
│ createdAt               │
│ TTL (ACTIVE: 30분)     │
└─────────────────────────┘
```

---

## 2. 엔티티 상세 정의

### 2.1 User (사용자)

**설명:** 콘서트 예약 시스템 사용자 정보

**테이블명:** `users`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| user_id | VARCHAR(100) | PK, NOT NULL | - | 사용자 고유 ID |
| name | VARCHAR(100) | NOT NULL | - | 사용자 이름 |
| email | VARCHAR(256) | NOT NULL, UNIQUE | - | 이메일 주소 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 계정 생성 시간 |

**인덱스:**
- PRIMARY KEY: `user_id`
- UNIQUE INDEX: `idx_user_email` ON `email`

**제약조건:**
- `user_id`는 최대 100자
- `email`은 중복 불가
- 계정 생성 시 자동으로 `Wallet` 생성 (애플리케이션 레벨)

---

### 2.2 Wallet (지갑)

**설명:** 사용자의 현금 및 포인트 잔액 정보

**테이블명:** `wallets`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| wallet_id | BIGINT | PK, AUTO_INCREMENT | - | 지갑 고유 ID |
| user_id | VARCHAR(100) | FK, NOT NULL, UNIQUE | - | 사용자 ID (users.user_id) |
| cash | INT | NOT NULL | 0 | 현금 잔액 |
| point | INT | NOT NULL | 0 | 포인트 잔액 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP ON UPDATE | 마지막 업데이트 시간 |

**인덱스:**
- PRIMARY KEY: `wallet_id`
- UNIQUE INDEX: `idx_wallet_user` ON `user_id`

**제약조건:**
- `user_id`는 `users.user_id` 외래 키 (CASCADE DELETE)
- `cash`, `point`는 음수 불가 (CHECK >= 0)
- 한 사용자당 하나의 지갑만 존재

---

### 2.3 Seat (좌석)

**설명:** 콘서트 좌석 정보 및 상태

**테이블명:** `seats`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| seat_id | VARCHAR(10) | PK, NOT NULL | - | 좌석 고유 ID (예: A-001) |
| seat_number | VARCHAR(20) | NOT NULL | - | 좌석 번호 (화면 표시용, 예: A-1) |
| price | INT | NOT NULL | - | 좌석 가격 |
| status | VARCHAR(20) | NOT NULL | 'AVAILABLE' | 좌석 상태 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 생성 시간 |
| updated_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP ON UPDATE | 업데이트 시간 |

**Enum 값:**
- status: `AVAILABLE`, `TEMP_RESERVED`, `CONFIRMED`

**인덱스:**
- PRIMARY KEY: `seat_id`
- INDEX: `idx_seat_status` ON `status`
- INDEX: `idx_seat_updated_at` ON `updated_at`

**제약조건:**
- `seat_id`는 최대 10자
- `price`는 양수 (CHECK > 0)
- `status`는 정의된 값만 허용

---

### 2.4 Reservation (예약)

**설명:** 좌석 예약 정보 (임시 예약 및 확정 예약)

**테이블명:** `reservations`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| reservation_id | VARCHAR(50) | PK, NOT NULL | - | 예약 고유 ID |
| user_id | VARCHAR(100) | FK, NOT NULL | - | 사용자 ID (users.user_id) |
| seat_id | VARCHAR(10) | FK, NOT NULL | - | 좌석 ID (seats.seat_id) |
| status | VARCHAR(20) | NOT NULL | 'TEMP_RESERVED' | 예약 상태 |
| price | INT | NOT NULL | - | 예약 시점 좌석 가격 |
| reserved_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 예약 시간 |
| expires_at | TIMESTAMP | NULL | - | 임시 예약 만료 시간 (5분 후) |
| confirmed_at | TIMESTAMP | NULL | - | 예약 확정 시간 (결제 완료 시) |

**Enum 값:**
- status: `TEMP_RESERVED`, `CONFIRMED`, `EXPIRED`, `CANCELLED`

**인덱스:**
- PRIMARY KEY: `reservation_id`
- INDEX: `idx_reservation_user` ON `user_id`
- INDEX: `idx_reservation_seat` ON `seat_id`
- INDEX: `idx_reservation_status` ON `status`
- INDEX: `idx_reservation_expires` ON `expires_at`
- UNIQUE INDEX: `idx_active_reservation` ON `seat_id, status` WHERE `status = 'TEMP_RESERVED' OR status = 'CONFIRMED'`

**제약조건:**
- `user_id`는 `users.user_id` 외래 키
- `seat_id`는 `seats.seat_id` 외래 키
- `status = TEMP_RESERVED`일 때 `expires_at` NOT NULL
- `status = CONFIRMED`일 때 `confirmed_at` NOT NULL
- 동일 좌석에 대해 TEMP_RESERVED 또는 CONFIRMED 상태 예약은 하나만 존재

---

### 2.5 Payment (결제)

**설명:** 결제 정보

**테이블명:** `payments`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| payment_id | VARCHAR(50) | PK, NOT NULL | - | 결제 고유 ID |
| reservation_id | VARCHAR(50) | FK, NOT NULL, UNIQUE | - | 예약 ID (reservations.reservation_id) |
| user_id | VARCHAR(100) | FK, NOT NULL | - | 사용자 ID (users.user_id) |
| total_amount | INT | NOT NULL | - | 총 결제 금액 |
| point_used | INT | NOT NULL | 0 | 사용한 포인트 |
| cash_used | INT | NOT NULL | - | 사용한 현금 |
| point_earned | INT | NOT NULL | 0 | 적립된 포인트 |
| paid_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 결제 완료 시간 |

**인덱스:**
- PRIMARY KEY: `payment_id`
- UNIQUE INDEX: `idx_payment_reservation` ON `reservation_id`
- INDEX: `idx_payment_user` ON `user_id`
- INDEX: `idx_payment_paid_at` ON `paid_at`

**제약조건:**
- `reservation_id`는 `reservations.reservation_id` 외래 키
- `user_id`는 `users.user_id` 외래 키
- `total_amount = point_used + cash_used`
- `point_earned = cash_used * 0.05` (5% 적립)
- 하나의 예약에 하나의 결제만 존재

---

### 2.6 PaymentHistory (결제 내역)

**설명:** 사용자의 모든 금액 변동 내역 (충전, 결제, 적립 등)

**테이블명:** `payment_histories`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| history_id | BIGINT | PK, AUTO_INCREMENT | - | 내역 고유 ID |
| wallet_id | BIGINT | FK, NOT NULL | - | 지갑 ID (wallets.wallet_id) |
| type | VARCHAR(20) | NOT NULL | - | 거래 유형 |
| amount | INT | NOT NULL | - | 변동 금액 (양수/음수) |
| cash_after | INT | NOT NULL | - | 거래 후 현금 잔액 |
| point_after | INT | NOT NULL | - | 거래 후 포인트 잔액 |
| description | VARCHAR(255) | NULL | - | 거래 설명 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 거래 시간 |

**Enum 값:**
- type: `CHARGE` (충전), `PAYMENT_CASH` (현금 결제), `PAYMENT_POINT` (포인트 결제), `POINT_EARN` (포인트 적립)

**인덱스:**
- PRIMARY KEY: `history_id`
- INDEX: `idx_history_wallet` ON `wallet_id`
- INDEX: `idx_history_created` ON `created_at`
- INDEX: `idx_history_type` ON `type` *(현재 구현에는 누락)*
- INDEX: `idx_wallet_created` ON `wallet_id, created_at` *(복합 인덱스)*

**제약조건:**
- `wallet_id`는 `wallets.wallet_id` 외래 키
- `amount`는 0이 아님
- 거래 내역은 삭제 불가 (Soft Delete 또는 불변)

**구현 참고:**
- 원래 스펙에서는 `balance_after` 하나의 필드였으나, 실제 구현에서는 `cash_after`와 `point_after`로 분리하여 더 명확하게 관리
- 이는 현금과 포인트를 별도로 추적할 수 있어 더 나은 설계임

---

### 2.7 TokenHistory (토큰 히스토리)

**설명:** 발급된 대기열 토큰의 이력 (감사 목적)

**테이블명:** `token_histories`

| 컬럼명 | 타입 | 제약조건 | 기본값 | 설명 |
|--------|------|---------|--------|------|
| token | VARCHAR(100) | PK, NOT NULL | - | 토큰 값 (UUID) |
| user_id | VARCHAR(100) | FK, NOT NULL | - | 사용자 ID (users.user_id) |
| status | VARCHAR(20) | NOT NULL | 'WAITING' | 토큰 상태 |
| created_at | TIMESTAMP | NOT NULL | CURRENT_TIMESTAMP | 토큰 발급 시간 |
| activated_at | TIMESTAMP | NULL | - | ACTIVE 상태 전환 시간 |
| expired_at | TIMESTAMP | NULL | - | 토큰 만료 시간 |

**Enum 값:**
- status: `WAITING`, `ACTIVE`, `EXPIRED`

**인덱스:**
- PRIMARY KEY: `token`
- INDEX: `idx_token_user` ON `user_id`
- INDEX: `idx_token_status` ON `status`
- INDEX: `idx_token_created` ON `created_at`
- INDEX: `idx_token_expired` ON `expired_at`

**제약조건:**
- `user_id`는 `users.user_id` 외래 키
- `status = ACTIVE`일 때 `activated_at` NOT NULL
- `status = EXPIRED`일 때 `expired_at` NOT NULL

---

## 3. 관계 정의

### 3.1 User ↔ Wallet (1:1)
- **관계:** 한 사용자는 하나의 지갑만 소유
- **외래 키:** `wallets.user_id` → `users.user_id`
- **Cascade:** DELETE CASCADE (사용자 삭제 시 지갑도 삭제)

### 3.2 User ↔ Reservation (1:N)
- **관계:** 한 사용자는 여러 예약을 가질 수 있음
- **외래 키:** `reservations.user_id` → `users.user_id`
- **Cascade:** DELETE RESTRICT (예약이 있는 사용자는 삭제 불가)

### 3.3 Seat ↔ Reservation (1:N)
- **관계:** 한 좌석은 여러 예약 기록을 가질 수 있음 (시간에 따라)
- **외래 키:** `reservations.seat_id` → `seats.seat_id`
- **Cascade:** DELETE RESTRICT (예약이 있는 좌석은 삭제 불가)
- **비즈니스 규칙:** 동일 시점에는 하나의 활성 예약만 존재

### 3.4 Reservation ↔ Payment (1:1)
- **관계:** 한 예약은 하나의 결제만 가짐
- **외래 키:** `payments.reservation_id` → `reservations.reservation_id`
- **Cascade:** DELETE RESTRICT (결제가 있는 예약은 삭제 불가)

### 3.5 Wallet ↔ PaymentHistory (1:N)
- **관계:** 한 지갑은 여러 거래 내역을 가짐
- **외래 키:** `payment_histories.wallet_id` → `wallets.wallet_id`
- **Cascade:** DELETE CASCADE (지갑 삭제 시 내역도 삭제)

### 3.6 User ↔ TokenHistory (1:N)
- **관계:** 한 사용자는 여러 토큰을 발급받을 수 있음 (시간에 따라)
- **외래 키:** `token_histories.user_id` → `users.user_id`
- **Cascade:** DELETE CASCADE (사용자 삭제 시 토큰 히스토리도 삭제)

---

## 4. 인덱스 전략

### 4.1 조회 성능 최적화

| 테이블 | 인덱스명 | 컬럼 | 용도 |
|--------|---------|------|------|
| users | idx_user_email | email | 이메일 중복 체크, 사용자 조회 |
| wallets | idx_wallet_user | user_id | 사용자별 지갑 조회 |
| seats | idx_seat_status | status | 예약 가능 좌석 필터링 |
| seats | idx_seat_updated_at | updated_at | 최근 업데이트된 좌석 조회 |
| reservations | idx_reservation_user | user_id | 사용자별 예약 조회 |
| reservations | idx_reservation_seat | seat_id | 좌석별 예약 이력 조회 |
| reservations | idx_reservation_status | status | 상태별 예약 필터링 |
| reservations | idx_reservation_expires | expires_at | 만료된 임시 예약 스케줄러 처리 |
| payments | idx_payment_user | user_id | 사용자별 결제 내역 조회 |
| payments | idx_payment_paid_at | paid_at | 시간별 결제 통계 |
| payment_histories | idx_history_wallet | wallet_id | 지갑별 거래 내역 조회 |
| payment_histories | idx_history_created | created_at | 시간별 거래 조회 |
| token_histories | idx_token_user | user_id | 사용자별 토큰 이력 조회 |
| token_histories | idx_token_expired | expired_at | 만료된 토큰 정리 |

### 4.2 복합 인덱스

| 테이블 | 인덱스명 | 컬럼 | 용도 |
|--------|---------|------|------|
| reservations | idx_active_reservation | seat_id, status | 좌석별 활성 예약 유일성 보장 |
| reservations | idx_user_status | user_id, status | 사용자별 상태별 예약 조회 |
| payment_histories | idx_wallet_type | wallet_id, type | 지갑별 거래 유형 필터링 |

---

## 5. 제약 조건

### 5.1 데이터 무결성

#### User
```sql
CHECK (LENGTH(user_id) > 0 AND LENGTH(user_id) <= 100)
CHECK (email LIKE '%@%')
```

#### Wallet
```sql
CHECK (cash >= 0)
CHECK (point >= 0)
```

#### Seat
```sql
CHECK (price > 0)
CHECK (status IN ('AVAILABLE', 'TEMP_RESERVED', 'CONFIRMED'))
```

#### Reservation
```sql
CHECK (price > 0)
CHECK (status IN ('TEMP_RESERVED', 'CONFIRMED', 'EXPIRED', 'CANCELLED'))
-- 임시 예약은 만료 시간 필수
CHECK (status != 'TEMP_RESERVED' OR expires_at IS NOT NULL)
-- 확정 예약은 확정 시간 필수
CHECK (status != 'CONFIRMED' OR confirmed_at IS NOT NULL)
```

#### Payment
```sql
CHECK (total_amount > 0)
CHECK (point_used >= 0)
CHECK (cash_used >= 0)
CHECK (point_earned >= 0)
CHECK (total_amount = point_used + cash_used)
```

### 5.2 비즈니스 로직 제약

#### 좌석 예약 동시성 제어
```sql
-- 동일 좌석에 활성 예약은 하나만 존재
CREATE UNIQUE INDEX idx_active_reservation
ON reservations(seat_id)
WHERE status IN ('TEMP_RESERVED', 'CONFIRMED');
```

#### 사용자당 활성 임시 예약 제한
```sql
-- 애플리케이션 레벨에서 제어
-- 한 사용자는 하나의 TEMP_RESERVED 예약만 가능
```

#### 토큰 유일성
```sql
-- 사용자당 활성 토큰은 하나만 존재 (Redis 레벨에서 제어)
-- RDB에는 히스토리만 저장
```

---

## 6. Redis 데이터 구조

### 6.1 대기열 토큰 (QueueToken)

**Key Pattern:** `queue:token:{token}`

**Data Structure:** Hash

**필드:**
```
userId: string
status: "WAITING" | "ACTIVE"
createdAt: timestamp
activatedAt: timestamp (ACTIVE일 때만)
```

**TTL:**
- WAITING: 무제한 (TTL 설정 안 함)
- ACTIVE: 1800초 (30분)

**Position 조회:**
- queuePosition은 Hash에 저장하지 않음
- 조회 시 `queue:waiting` Sorted Set의 ZRANK 명령어로 동적 계산
- ZRANK 반환값(0-based) + 1 = 실제 대기 순번(1-based)

**Example:**
```
queue:token:550e8400-e29b-41d4-a716-446655440000
{
  userId: "user123",
  status: "ACTIVE",
  createdAt: "2025-11-03T13:00:00Z",
  activatedAt: "2025-11-03T13:00:00Z"
}
TTL: 1800
```

**Position 조회 예시:**
```
Redis: ZRANK queue:waiting "550e8400-e29b-41d4-a716-446655440001"
→ 반환값: 22 (0-based rank)
→ 실제 대기 순번: 23번
```

---

### 6.2 대기열 (Waiting Queue)

**Key Pattern:** `queue:waiting`

**Data Structure:** Sorted Set (ZSET)

**Score:** 생성 시간 (timestamp)

**Value:** token

**목적:** FIFO 순서 보장, 순번 조회

**Example:**
```
queue:waiting
[
  (1730620800, "550e8400-e29b-41d4-a716-446655440001"),
  (1730620801, "550e8400-e29b-41d4-a716-446655440002"),
  (1730620802, "550e8400-e29b-41d4-a716-446655440003")
]
```

---

### 6.3 활성 토큰 Set (Active Token Set)

**Key Pattern:** `queue:active`

**Data Structure:** Set

**Value:** token

**용도:**
- ACTIVE 상태 토큰 관리
- 활성 사용자 수 조회 (SCARD 명령어, O(1))
- TTL 만료된 토큰 자동 정리 (스케줄러)

**Example:**
```
queue:active
Set ["550e8400-e29b-41d4-a716-446655440010",
     "550e8400-e29b-41d4-a716-446655440011",
     "550e8400-e29b-41d4-a716-446655440012"]
```

**활성 사용자 수 조회:**
```redis
SCARD queue:active  # 487
```

**동기화 로직:**
- 스케줄러(10초마다)가 Set의 각 토큰이 실제 Hash에 존재하는지 확인
- Hash가 없으면 (TTL 만료) Set에서 자동 제거
- 이를 통해 TTL 만료와 Set 동기화

---

### 6.4 사용자별 토큰 매핑

**Key Pattern:** `queue:user:{userId}`

**Data Structure:** String

**Value:** token

**TTL:** 토큰과 동일

**용도:** 사용자당 하나의 토큰만 발급되도록 보장

**Example:**
```
queue:user:user123 = "550e8400-e29b-41d4-a716-446655440000"
TTL: 1800
```

---

## 7. 구현 상태 (2025-11-13 업데이트)

### 완료된 항목
- ✅ 모든 엔티티 정의 완료 (User, Wallet, Seat, Reservation, Payment, PaymentHistory, TokenHistory)
- ✅ 외래 키 관계 설정 완료
- ✅ 인덱스 대부분 구현 완료
- ✅ Bean Validation 적용 (@Positive, @PositiveOrZero)
- ✅ JPA Auditing 설정 (@CreatedDate, @LastModifiedDate)

### 남은 작업
- ⚠️ PaymentHistory의 type 필드에 @Enumerated(EnumType.STRING) 추가 필요
- ⚠️ Reservation의 idx_user_status 복합 인덱스 활성화 필요 (현재 주석 처리됨)
- ⚠️ PaymentHistory의 idx_history_type 인덱스 추가 필요
- ⚠️ Redis 데이터 구조 구현 및 연동

### 스펙과의 차이점
- **PaymentHistory**: `balance_after` → `cash_after`, `point_after`로 분리 구현 (더 명확한 설계)

---

## 8. 데이터 흐름 예시

### 8.1 사용자 등록 및 충전
```
1. INSERT INTO users (user_id, name, email)
2. INSERT INTO wallets (user_id, cash=0, point=0)
3. UPDATE wallets SET cash = cash + 100000 WHERE user_id = 'user123'
4. INSERT INTO payment_histories (wallet_id, type='CHARGE', amount=100000, ...)
```

### 8.2 대기열 토큰 발급
```
1. Redis: HSET queue:token:{token} userId {userId} status WAITING createdAt {timestamp}
2. Redis: SET queue:user:{userId} {token}
3. Redis: ZADD queue:waiting {timestamp} {token}
4. RDB: INSERT INTO token_histories (token, user_id, status='WAITING', ...)
```

**참고**: 동일 사용자가 재발급 요청 시 기존 토큰 무효화 후 새 토큰 발급

### 8.3 대기열 입장 (WAITING → ACTIVE)
```
1. Redis: ZRANGE queue:waiting 0 9 (상위 10명 조회)
2. Redis: ZREM queue:waiting tokens (대기열에서 제거)
3. Redis: SADD queue:active {token} (활성 토큰 Set에 추가)
4. Redis: HSET queue:token:{token} status ACTIVE, activatedAt timestamp
5. Redis: EXPIRE queue:token:{token} 1800 (30분 TTL 설정)
6. RDB: UPDATE token_histories SET status='ACTIVE', activated_at=NOW() WHERE token=?
```

### 8.4 좌석 임시 예약
```
1. BEGIN TRANSACTION
2. SELECT * FROM seats WHERE seat_id=? FOR UPDATE (비관적 락)
3. CHECK seat.status = 'AVAILABLE'
4. INSERT INTO reservations (reservation_id, user_id, seat_id, status='TEMP_RESERVED', expires_at=NOW()+5min)
5. UPDATE seats SET status='TEMP_RESERVED', updated_at=NOW() WHERE seat_id=?
6. COMMIT
```

### 8.5 결제 및 예약 확정
```
1. BEGIN TRANSACTION
2. SELECT * FROM reservations WHERE reservation_id=? FOR UPDATE
3. CHECK reservation.status = 'TEMP_RESERVED' AND reservation.expires_at > NOW()
4. SELECT * FROM wallets WHERE user_id=? FOR UPDATE
5. VALIDATE wallet.cash + wallet.point >= reservation.price
6. CALCULATE point_used, cash_used, point_earned
7. UPDATE wallets SET cash=cash-cash_used, point=point-point_used+point_earned WHERE user_id=?
8. INSERT INTO payments (payment_id, reservation_id, total_amount, point_used, cash_used, point_earned, ...)
9. INSERT INTO payment_histories (wallet_id, type='PAYMENT_CASH', amount=-cash_used, ...)
10. INSERT INTO payment_histories (wallet_id, type='PAYMENT_POINT', amount=-point_used, ...)
11. INSERT INTO payment_histories (wallet_id, type='POINT_EARN', amount=point_earned, ...)
12. UPDATE reservations SET status='CONFIRMED', confirmed_at=NOW() WHERE reservation_id=?
13. UPDATE seats SET status='CONFIRMED', updated_at=NOW() WHERE seat_id=?
14. Redis: SREM queue:active {token} (활성 토큰 Set에서 제거)
15. Redis: DEL queue:token:{token} (토큰 Hash 삭제)
16. Redis: DEL queue:user:{userId} (사용자 토큰 매핑 제거)
17. RDB: UPDATE token_histories SET status='EXPIRED', expired_at=NOW() WHERE token=?
18. COMMIT
```

---

## 9. 스케줄러 작업

### 9.1 임시 예약 만료 처리
```sql
-- 5분 경과된 TEMP_RESERVED 예약 조회
SELECT reservation_id, seat_id
FROM reservations
WHERE status = 'TEMP_RESERVED'
  AND expires_at < NOW();

-- 예약 상태 변경
UPDATE reservations
SET status = 'EXPIRED'
WHERE reservation_id IN (...);

-- 좌석 상태 복원
UPDATE seats
SET status = 'AVAILABLE', updated_at = NOW()
WHERE seat_id IN (...);
```

### 9.2 대기열 입장 처리 (10초마다)
```
1. Redis: SMEMBERS queue:active (활성 토큰 Set 조회)
2. FOR EACH token IN active_tokens:
3.   IF NOT EXISTS queue:token:{token}:
4.     Redis: SREM queue:active {token} (TTL 만료된 토큰 정리)
5. Redis: SCARD queue:active (정리 후 활성 사용자 수)
6. IF active_count < 500:
7.   Redis: ZRANGE queue:waiting 0 9 (상위 10명)
8.   FOR EACH token:
9.     Redis: ZREM queue:waiting token
10.    Redis: SADD queue:active token (활성 Set에 추가)
11.    Redis: HSET queue:token:{token} status ACTIVE, activatedAt NOW()
12.    Redis: EXPIRE queue:token:{token} 1800
13.    RDB: UPDATE token_histories SET status='ACTIVE', activated_at=NOW()
```

**참고:**
- 먼저 TTL 만료된 토큰을 Set에서 정리 (동기화)
- 정리 후 실제 활성 사용자 수로 입장 여부 판단

### 9.3 만료된 ACTIVE 토큰 정리
```
-- TTL로 Hash는 자동 삭제됨
-- queue:active Set 정리는 8.2 스케줄러에서 처리 (10초마다)
-- 즉, 최대 10초 지연 후 동기화됨

-- 선택사항: Keyspace Notification 리스너로 즉시 처리
1. 만료 이벤트 수신: __keyevent@0__:expired
2. IF key.startsWith("queue:token:"):
3.   token = extractToken(key)
4.   Redis: SREM queue:active {token}
5.   RDB: UPDATE token_histories SET status='EXPIRED', expired_at=NOW()
```

**참고:**
- **동기화 방식**: 스케줄러가 10초마다 Hash 존재 여부 확인 후 Set 정리
- **지연 시간**: 최대 10초 (TTL 만료 시점과 다음 스케줄러 실행 사이)
- **정확성**: 활성 사용자 수 조회 시 항상 동기화 후 측정하므로 정확함

---

## 10. 성능 최적화 고려사항

### 10.1 인덱스 최적화
- 자주 조회되는 컬럼에 인덱스 생성
- 복합 인덱스를 활용한 커버링 인덱스 전략
- 불필요한 인덱스 제거 (INSERT 성능 영향)

### 10.2 파티셔닝
- `payment_histories`: 날짜 기반 파티셔닝 (월별)
- `token_histories`: 날짜 기반 파티셔닝 (월별)

### 10.3 캐싱 전략
- Redis: 대기열 정보, 활성 토큰 (휘발성)
- 좌석 상태는 RDB 우선 (데이터 정합성)

### 10.4 동시성 제어
- 좌석 예약: 비관적 락 (SELECT FOR UPDATE)
- 잔액 차감: 비관적 락 (트랜잭션 격리 레벨 READ COMMITTED 이상)

---

## 11. 데이터 보존 정책

| 데이터 | 보존 기간 | 정책 |
|--------|----------|------|
| users | 영구 | 회원 탈퇴 시 익명화 처리 |
| wallets | 영구 | 사용자와 함께 삭제 |
| seats | 영구 | - |
| reservations | 1년 | 1년 경과 후 아카이빙 |
| payments | 5년 | 법적 요구사항에 따라 보관 |
| payment_histories | 5년 | 법적 요구사항에 따라 보관 |
| token_histories | 3개월 | 감사 목적, 3개월 후 삭제 |
| Redis (queue) | 실시간 | TTL 기반 자동 만료 |

---

## 12. 마이그레이션 전략

### 12.1 초기 데이터
```sql
-- 좌석 초기 데이터 (50개 좌석 예시)
INSERT INTO seats (seat_id, seat_number, price, status) VALUES
('A-001', 'A-1', 50000, 'AVAILABLE'),
('A-002', 'A-2', 50000, 'AVAILABLE'),
...
('A-050', 'A-50', 50000, 'AVAILABLE');
```

### 12.2 DDL 실행 순서
1. users
2. wallets (users FK)
3. seats
4. reservations (users, seats FK)
5. payments (reservations, users FK)
6. payment_histories (wallets FK)
7. token_histories (users FK)
8. 인덱스 생성
9. 제약 조건 추가
