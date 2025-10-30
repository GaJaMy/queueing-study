# 🎯 콘서트 예약 시스템 — RESTful API 명세서

---

## 1. 대기열 시스템 (Queue System)

### 🧍‍♂️ 대기 토큰 발급
**Endpoint:** /api/queue/token  
**Method:** POST  
**Description:** 예약 페이지 접근 시 대기 토큰 발급

**Request Body:**  
{
"userId": "string"
}

**Response Body:**  
{
"token": "string",
"expiresAt": "ISO8601 timestamp",
"queuePosition": 1
}

**Status Codes:**
- 201 Created : 토큰 발급 성공
- 400 Bad Request : 사용자 정보 누락
- 429 Too Many Requests : 이미 대기열에 존재

---

### 🧍‍♀️ 대기 순서 확인
**Endpoint:** /api/queue/status  
**Method:** GET  
**Query Params:** token=string  
**Description:** 토큰으로 대기 순서 및 입장 가능 여부 확인

**Response Body:**  
{
"queuePosition": 5,
"estimatedWaitTimeSeconds": 30
"available": true
}

**Status Codes:**
- 200 OK : 조회 성공
- 401 Unauthorized : 토큰이 유효하지 않음
- 404 Not Found : 토큰이 존재하지 않음

---

## 2. 좌석 예약 시스템 (Seat Reservation System)

### 🎫 좌석 상태 조회
**Endpoint:** /api/seats  
**Method:** GET  
**Query Params:** token=string  
**Description:** 좌석 상태 전체 조회

**Response Body:**  
[
{
"seatId": "A1",
"status": "AVAILABLE"
},
{
"seatId": "A2",
"status": "RESERVED"
}
]

**Status Codes:**
- 200 OK : 조회 성공
- 401 Unauthorized : 토큰 만료 또는 유효하지 않음

---

### 🪑 임시 좌석 예약
**Endpoint:** /api/seats/temp-reserve  
**Method:** POST  
**Description:** AVAILABLE 좌석을 TEMP_RESERVED로 변경

**Request Body:**  
{
"token": "string",
"seatId": "A1"
}

**Response Body:**  
{
"seatId": "A1",
"status": "TEMP_RESERVED",
"expiresAt": "ISO8601 timestamp"
}

**Status Codes:**
- 200 OK : 임시 예약 성공
- 400 Bad Request : 좌석 상태가 예약 불가
- 401 Unauthorized : 토큰 만료
- 409 Conflict : 동시 예약 발생

---

### 💰 결제 및 예약 확정
**Endpoint:** /api/seats/reserve  
**Method:** POST  
**Description:** 결제 완료 후 TEMP_RESERVED → RESERVED

**Request Body:**  
{
"token": "string",
"seatId": "A1",
"paymentInfo": {
"method": "CARD",
"amount": 100000
}
}

**Response Body:**  
{
"seatId": "A1",
"status": "RESERVED",
"reservedAt": "ISO8601 timestamp"
}

**Status Codes:**
- 200 OK : 결제 및 예약 성공
- 400 Bad Request : 결제 실패, 좌석 상태 불일치
- 401 Unauthorized : 토큰 만료
- 409 Conflict : 다른 사용자가 결제 완료

---

## 3. 토큰 및 임시 예약 정책

### ⏳ 토큰 만료
- 토큰 만료 시 /api/seats 조회, 임시 예약, 결제 요청 모두 401 Unauthorized 반환

### 🔁 임시 예약 타임아웃
- 임시 예약 좌석은 TTL 만료 시 자동 AVAILABLE로 전환
- 사용자 결제 실패 시 409 Conflict 또는 400 Bad Request 반환

---

## 4. 공통 Status Codes 및 에러 처리

| 상태 코드 | 의미 | 예시 메시지 |
|-----------|------|------------|
| 200 OK | 정상 처리 | "Success", "임시 예약 성공" |
| 201 Created | 자원 생성 완료 | "Token issued" |
| 400 Bad Request | 요청 오류 | "Invalid seat or payment info", "좌석 상태가 예약 불가" |
| 401 Unauthorized | 토큰 만료 또는 무효 | "Token expired", "토큰 만료" |
| 404 Not Found | 리소스 없음 | "Token not found" |
| 409 Conflict | 동시 접근 충돌 | "Seat already reserved", "동시 예약 발생" |
| 429 Too Many Requests | 동시 입장 제한 | "Queue is full" |
| 500 Internal Server Error | 서버 오류 | "Unexpected error" |
