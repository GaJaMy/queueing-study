# RESTful API 명세서

## 목차
1. [대기열 API](#1-대기열-api)
2. [좌석 API](#2-좌석-api)
3. [결제 API](#3-결제-api)
4. [사용자 및 지갑 API](#4-사용자-및-지갑-api)
5. [공통 응답 형식](#5-공통-응답-형식)
6. [에러 코드](#6-에러-코드)
7. [HTTP 상태 코드](#7-http-상태-코드)

---

## 1. 대기열 API

### 1.1 대기열 토큰 발급

**Endpoint:** `POST /v1/queue/token`

**Description:** 대기열에 참여하여 고유한 토큰을 발급받습니다.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "string"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| userId | string | O | 사용자 ID |

**Response (201 Created):**
```json
{
  "errorCode": "SU000",
  "msg": "토큰 발급 성공",
  "data": {
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ACTIVE",
    "queuePosition": 0,
    "estimatedWaitTime": 0,
    "expiresAt": "2025-11-03T13:30:00Z"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| token | string | UUID 기반 고유 토큰 |
| status | string | 토큰 상태 (WAITING, ACTIVE) |
| queuePosition | integer | 현재 대기 순번 (0이면 즉시 입장) |
| estimatedWaitTime | integer | 예상 대기 시간(초) |
| expiresAt | string (ISO 8601) | 만료 시간 (ACTIVE 상태일 경우만, WAITING은 null) |

**Response (200 OK) - 대기 상태:**
```json
{
  "errorCode": "SU000",
  "msg": "대기열에 등록되었습니다",
  "data": {
    "token": "550e8400-e29b-41d4-a716-446655440001",
    "status": "WAITING",
    "queuePosition": 23,
    "estimatedWaitTime": 240,
    "expiresAt": null
  }
}
```

**Error Responses:**
- `409 Conflict` - 이미 토큰이 발급된 사용자
```json
{
  "errorCode": "TK001",
  "msg": "이미 발급된 토큰이 존재합니다.",
  "data": null
}
```

- `404 Not Found` - 존재하지 않는 사용자
```json
{
  "errorCode": "US000",
  "msg": "존재하지 않는 유저 입니다.",
  "data": null
}
```

---

### 1.2 대기 순서 조회

**Endpoint:** `GET /v1/queue/status`

**Description:** 현재 대기 순번과 토큰 상태를 조회합니다.

**Request Headers:**
```
X-Queue-Token: {token}
```

**Response (200 OK) - WAITING 상태:**
```json
{
  "errorCode": "SU000",
  "msg": "ok",
  "data": {
    "token": "550e8400-e29b-41d4-a716-446655440001",
    "status": "WAITING",
    "queuePosition": 15,
    "remainingWaitCount": 14,
    "estimatedWaitTime": 150,
    "expiresAt": null
  }
}
```

**Response (200 OK) - ACTIVE 상태:**
```json
{
  "errorCode": "SU000",
  "msg": "ok",
  "data": {
    "token": "550e8400-e29b-41d4-a716-446655440000",
    "status": "ACTIVE",
    "queuePosition": 0,
    "remainingWaitCount": 0,
    "remainingTime": 1650,
    "expiresAt": "2025-11-03T13:30:00Z"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| status | string | WAITING (대기 중) / ACTIVE (입장 완료) |
| queuePosition | integer | 현재 대기 순번 (ACTIVE는 0) |
| remainingWaitCount | integer | 앞에 남은 대기 인원 |
| estimatedWaitTime | integer | 예상 대기 시간(초) - WAITING만 |
| remainingTime | integer | 남은 유효 시간(초) - ACTIVE만 |
| expiresAt | string | 만료 시간 (ACTIVE만, WAITING은 null) |

**Error Responses:**
- `401 Unauthorized` - 만료된 토큰
```json
{
  "errorCode": "TOKEN_EXPIRED",
  "msg": "토큰이 만료되었습니다.",
  "data": null
}
```

- `404 Not Found` - 유효하지 않은 토큰
```json
{
  "errorCode": "TOKEN_INVALID",
  "msg": "유효하지 않은 토큰입니다.",
  "data": null
}
```

---

## 2. 좌석 API

### 2.1 예약 가능한 좌석 목록 조회

**Endpoint:** `GET /v1/seats`

**Description:** 예약 가능한 좌석 목록을 조회합니다. (ACTIVE 토큰 필수)

**Request Headers:**
```
X-Queue-Token: {token}
```

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| status | string | X | 좌석 상태 필터 (AVAILABLE, TEMP_RESERVED, CONFIRMED) |

**Response (200 OK):**
```json
{
  "errorCode": "SU000",
  "msg": "ok",
  "data": {
    "seats": [
      {
        "seatId": "A-001",
        "seatNumber": "A-1",
        "price": 50000,
        "status": "AVAILABLE"
      },
      {
        "seatId": "A-002",
        "seatNumber": "A-2",
        "price": 50000,
        "status": "TEMP_RESERVED"
      },
      {
        "seatId": "A-003",
        "seatNumber": "A-3",
        "price": 50000,
        "status": "CONFIRMED"
      }
    ],
    "totalCount": 50,
    "availableCount": 23
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| seatId | string | 좌석 고유 ID |
| seatNumber | string | 좌석 번호 (화면 표시용) |
| price | integer | 좌석 가격 |
| status | string | AVAILABLE / TEMP_RESERVED / CONFIRMED |
| totalCount | integer | 전체 좌석 수 |
| availableCount | integer | 예약 가능한 좌석 수 |

**Error Responses:**
- `401 Unauthorized` - WAITING 상태 토큰으로 요청
```json
{
  "errorCode": "TOKEN_NOT_ACTIVE",
  "msg": "입장이 완료되지 않은 토큰입니다.",
  "data": null
}
```

- `401 Unauthorized` - 만료된 토큰
```json
{
  "errorCode": "TOKEN_EXPIRED",
  "msg": "토큰이 만료되었습니다.",
  "data": null
}
```

---

### 2.2 좌석 임시 예약

**Endpoint:** `POST /v1/seats/reserve`

**Description:** 좌석을 임시로 예약합니다. (5분간 점유)

**Request Headers:**
```
X-Queue-Token: {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "seatId": "A-001",
  "userId": "user123"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| seatId | string | O | 좌석 ID |
| userId | string | O | 사용자 ID |

**Response (201 Created):**
```json
{
  "errorCode": "SU000",
  "msg": "좌석이 임시 예약되었습니다.",
  "data": {
    "reservationId": "res-12345",
    "seatId": "A-001",
    "seatNumber": "A-1",
    "status": "TEMP_RESERVED",
    "userId": "user123",
    "price": 50000,
    "reservedAt": "2025-11-03T13:00:00Z",
    "expiresAt": "2025-11-03T13:05:00Z"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| reservationId | string | 예약 ID |
| status | string | 좌석 상태 (TEMP_RESERVED) |
| reservedAt | string | 예약 시간 |
| expiresAt | string | 임시 예약 만료 시간 (5분 후) |

**Error Responses:**
- `400 Bad Request` - 예약 불가능한 좌석
```json
{
  "errorCode": "SEAT_NOT_AVAILABLE",
  "msg": "해당 좌석은 예약할 수 없습니다.",
  "data": null
}
```

- `409 Conflict` - 동시 예약 시도
```json
{
  "errorCode": "SEAT_ALREADY_RESERVED",
  "msg": "다른 사용자가 이미 예약한 좌석입니다.",
  "data": null
}
```

- `409 Conflict` - 이미 임시 예약한 좌석 존재
```json
{
  "errorCode": "ALREADY_HAS_RESERVATION",
  "msg": "이미 임시 예약한 좌석이 있습니다.",
  "data": null
}
```

- `401 Unauthorized` - 토큰 만료
```json
{
  "errorCode": "TOKEN_EXPIRED",
  "msg": "토큰이 만료되었습니다.",
  "data": null
}
```

---

## 3. 결제 API

### 3.1 결제 처리

**Endpoint:** `POST /v1/payment`

**Description:** 임시 예약된 좌석에 대해 결제를 처리하고 예약을 확정합니다.

**Request Headers:**
```
X-Queue-Token: {token}
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user123",
  "reservationId": "res-12345",
  "usePoint": true
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| userId | string | O | 사용자 ID |
| reservationId | string | O | 예약 ID |
| usePoint | boolean | O | 포인트 사용 여부 (true/false) |

**Response (200 OK) - 포인트 사용:**
```json
{
  "errorCode": "SU000",
  "msg": "결제가 완료되었습니다.",
  "data": {
    "paymentId": "pay-67890",
    "reservationId": "res-12345",
    "seatId": "A-001",
    "seatNumber": "A-1",
    "status": "CONFIRMED",
    "totalAmount": 50000,
    "payment": {
      "pointUsed": 10000,
      "cashUsed": 40000,
      "pointEarned": 2000
    },
    "balance": {
      "remainingCash": 60000,
      "remainingPoint": 2000
    },
    "paidAt": "2025-11-03T13:03:00Z"
  }
}
```

**Response (200 OK) - 포인트 미사용:**
```json
{
  "errorCode": "SU000",
  "msg": "결제가 완료되었습니다.",
  "data": {
    "paymentId": "pay-67891",
    "reservationId": "res-12346",
    "seatId": "A-002",
    "seatNumber": "A-2",
    "status": "CONFIRMED",
    "totalAmount": 50000,
    "payment": {
      "pointUsed": 0,
      "cashUsed": 50000,
      "pointEarned": 2500
    },
    "balance": {
      "remainingCash": 50000,
      "remainingPoint": 12500
    },
    "paidAt": "2025-11-03T13:03:00Z"
  }
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| paymentId | string | 결제 ID |
| status | string | 좌석 상태 (CONFIRMED) |
| totalAmount | integer | 총 결제 금액 |
| pointUsed | integer | 사용한 포인트 |
| cashUsed | integer | 사용한 현금 |
| pointEarned | integer | 적립된 포인트 (현금 사용액의 5%) |
| remainingCash | integer | 남은 현금 잔액 |
| remainingPoint | integer | 남은 포인트 잔액 |
| paidAt | string | 결제 완료 시간 |

**Error Responses:**
- `400 Bad Request` - 잔액 부족
```json
{
  "errorCode": "INSUFFICIENT_BALANCE",
  "msg": "잔액이 부족합니다.",
  "data": {
    "required": 50000,
    "available": 45000
  }
}
```

- `404 Not Found` - 예약 정보 없음
```json
{
  "errorCode": "RESERVATION_NOT_FOUND",
  "msg": "예약 정보를 찾을 수 없습니다.",
  "data": null
}
```

- `400 Bad Request` - 임시 예약 만료
```json
{
  "errorCode": "TEMP_RESERVATION_EXPIRED",
  "msg": "임시 예약 시간이 만료되었습니다.",
  "data": null
}
```

- `401 Unauthorized` - 토큰 만료
```json
{
  "errorCode": "TOKEN_EXPIRED",
  "msg": "토큰이 만료되었습니다.",
  "data": null
}
```

---

## 4. 사용자 및 지갑 API

### 4.1 사용자 등록

**Endpoint:** `POST /v1/users`

**Description:** 신규 사용자를 등록합니다.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user123",
  "name": "홍길동",
  "email": "hong@example.com"
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| userId | string | O | 사용자 ID (최대 100자) |
| name | string | O | 이름 (최대 100자) |
| email | string | O | 이메일 (최대 256자) |

**Response (201 Created):**
```json
{
  "errorCode": "SU000",
  "msg": "사용자가 등록되었습니다.",
  "data": {
    "userId": "user123",
    "name": "홍길동",
    "email": "hong@example.com",
    "wallet": {
      "walletId": 1,
      "cash": 0,
      "point": 0
    },
    "createdAt": "2025-11-03T13:00:00Z"
  }
}
```

**Error Responses:**
- `409 Conflict` - 중복된 사용자 ID
```json
{
  "errorCode": "USER_ALREADY_EXISTS",
  "msg": "이미 존재하는 사용자 ID입니다.",
  "data": null
}
```

---

### 4.2 잔액 충전

**Endpoint:** `POST /v1/wallet/charge`

**Description:** 지갑에 현금을 충전합니다.

**Request Headers:**
```
Content-Type: application/json
```

**Request Body:**
```json
{
  "userId": "user123",
  "amount": 100000
}
```

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| userId | string | O | 사용자 ID |
| amount | integer | O | 충전 금액 (양수) |

**Response (200 OK):**
```json
{
  "errorCode": "SU000",
  "msg": "충전이 완료되었습니다.",
  "data": {
    "walletId": 1,
    "userId": "user123",
    "cash": 100000,
    "point": 5000,
    "chargedAmount": 100000,
    "updatedAt": "2025-11-03T13:05:00Z"
  }
}
```

**Error Responses:**
- `400 Bad Request` - 잘못된 충전 금액
```json
{
  "errorCode": "INVALID_AMOUNT",
  "msg": "충전 금액은 양수여야 합니다.",
  "data": null
}
```

- `404 Not Found` - 존재하지 않는 사용자
```json
{
  "errorCode": "US000",
  "msg": "존재하지 않는 유저 입니다.",
  "data": null
}
```

---

### 4.3 잔액 조회

**Endpoint:** `GET /v1/wallet/{userId}`

**Description:** 사용자의 현금 및 포인트 잔액을 조회합니다.

**Path Parameters:**
| 파라미터 | 타입 | 필수 | 설명 |
|---------|------|------|------|
| userId | string | O | 사용자 ID |

**Response (200 OK):**
```json
{
  "errorCode": "SU000",
  "msg": "ok",
  "data": {
    "walletId": 1,
    "userId": "user123",
    "cash": 100000,
    "point": 5000,
    "updatedAt": "2025-11-03T13:05:00Z"
  }
}
```

**Error Responses:**
- `404 Not Found` - 존재하지 않는 사용자
```json
{
  "errorCode": "US000",
  "msg": "존재하지 않는 유저 입니다.",
  "data": null
}
```

- `500 Internal Server Error` - 지갑 정보 없음
```json
{
  "errorCode": "SY001",
  "msg": "서버 에러",
  "data": null
}
```

---

## 5. 공통 응답 형식

모든 API 응답은 다음 형식을 따릅니다:

```json
{
  "errorCode": "string",
  "msg": "string",
  "data": object | array | null
}
```

| 필드 | 타입 | 설명 |
|------|------|------|
| errorCode | string | 에러 코드 (성공 시 "SU000") |
| msg | string | 응답 메시지 |
| data | object/array/null | 응답 데이터 |

---

## 6. 에러 코드

### 6.1 사용자 관련
| 에러 코드 | HTTP 상태 | 메시지 |
|----------|----------|--------|
| US000 | 404 | 존재하지 않는 유저 입니다. |
| USER_ALREADY_EXISTS | 409 | 이미 존재하는 사용자 ID입니다. |

### 6.2 토큰 관련
| 에러 코드 | HTTP 상태 | 메시지 |
|----------|----------|--------|
| TOKEN_EXPIRED | 401 | 토큰이 만료되었습니다. |
| TOKEN_INVALID | 404 | 유효하지 않은 토큰입니다. |
| TOKEN_NOT_ACTIVE | 401 | 입장이 완료되지 않은 토큰입니다. |
| TK001 | 409 | 이미 발급된 토큰이 존재합니다. |

### 6.3 좌석 관련
| 에러 코드 | HTTP 상태 | 메시지 |
|----------|----------|--------|
| SEAT_NOT_AVAILABLE | 400 | 해당 좌석은 예약할 수 없습니다. |
| SEAT_ALREADY_RESERVED | 409 | 다른 사용자가 이미 예약한 좌석입니다. |
| ALREADY_HAS_RESERVATION | 409 | 이미 임시 예약한 좌석이 있습니다. |

### 6.4 결제 관련
| 에러 코드 | HTTP 상태 | 메시지 |
|----------|----------|--------|
| INSUFFICIENT_BALANCE | 400 | 잔액이 부족합니다. |
| RESERVATION_NOT_FOUND | 404 | 예약 정보를 찾을 수 없습니다. |
| TEMP_RESERVATION_EXPIRED | 400 | 임시 예약 시간이 만료되었습니다. |
| INVALID_AMOUNT | 400 | 충전 금액은 양수여야 합니다. |

### 6.5 시스템 관련
| 에러 코드 | HTTP 상태 | 메시지 |
|----------|----------|--------|
| SU000 | 200 | ok |
| SY001 | 500 | 서버 에러 |

---

## 7. HTTP 상태 코드

### 7.1 성공 응답
| 상태 코드 | 설명 | 사용 예 |
|----------|------|--------|
| 200 OK | 요청 성공 | 조회, 업데이트 성공 |
| 201 Created | 리소스 생성 성공 | 토큰 발급, 예약 생성, 사용자 등록 |

### 7.2 클라이언트 오류
| 상태 코드 | 설명 | 사용 예 |
|----------|------|--------|
| 400 Bad Request | 잘못된 요청 | 잔액 부족, 예약 불가 좌석, 잘못된 입력 |
| 401 Unauthorized | 인증 실패 | 토큰 만료, 토큰 미활성화 |
| 404 Not Found | 리소스 없음 | 사용자 없음, 예약 없음, 토큰 없음 |
| 409 Conflict | 리소스 충돌 | 중복 예약, 동시 예약 시도, 이미 존재하는 사용자 |

### 7.3 서버 오류
| 상태 코드 | 설명 | 사용 예 |
|----------|------|--------|
| 500 Internal Server Error | 서버 내부 오류 | 예상치 못한 에러 |

---

## 8. 인증 방식

### 8.1 토큰 헤더
대기열 관련 API를 제외한 모든 요청에는 토큰 헤더가 필요합니다:

```
X-Queue-Token: {token}
```

### 8.2 토큰 상태 검증
- **WAITING 상태**: 대기 순서 조회만 가능
- **ACTIVE 상태**: 모든 좌석/결제 API 사용 가능
- **EXPIRED**: 모든 요청 거부 (401 Unauthorized)

---

## 9. 성능 요구사항

| API | 목표 응답 시간 |
|-----|--------------|
| GET /v1/queue/status | 100ms 이내 |
| POST /v1/seats/reserve | 200ms 이내 |
| POST /v1/payment | 500ms 이내 |
| 기타 API | 300ms 이내 |

---

## 10. API 호출 예시 시나리오

### 시나리오: 사용자가 콘서트 좌석을 예약하고 결제하는 전체 흐름

```
1. 사용자 등록
   POST /v1/users
   → 사용자 계정 생성, 빈 지갑 생성

2. 잔액 충전
   POST /v1/wallet/charge
   → 현금 100,000원 충전

3. 대기열 토큰 발급
   POST /v1/queue/token
   → WAITING 상태 토큰 발급 (대기 순번 23번)

4. 대기 순서 조회 (주기적 폴링)
   GET /v1/queue/status
   → 대기 순번 확인, ACTIVE 상태 전환 대기

5. 입장 완료 (ACTIVE 상태로 전환)
   GET /v1/queue/status
   → status: ACTIVE, 남은 시간 30분

6. 좌석 목록 조회
   GET /v1/seats
   → 예약 가능한 좌석 목록 조회

7. 좌석 임시 예약
   POST /v1/seats/reserve
   → A-001 좌석 임시 예약 (5분간 점유)

8. 결제 처리
   POST /v1/payment (usePoint: true)
   → 포인트 + 현금 결제, 좌석 확정, 토큰 만료

9. 잔액 조회
   GET /v1/wallet/{userId}
   → 남은 잔액 및 적립 포인트 확인
```

---

## 11. 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|---------|
| 1.0.0 | 2025-11-03 | 초기 API 명세서 작성 |
