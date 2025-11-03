# 엔티티 수정 필요 사항

## 개요
data-model.md 스펙과 실제 구현된 엔티티를 비교 검증한 결과, 총 **40개 이상의 문제점**이 발견되었습니다.

### 문제 분류
1. **필드 타입 및 매핑 오류**: 16개
2. **인덱스 누락**: 20개 이상
3. **CHECK 제약조건 누락**: 8개
4. **기타 제약조건**: 2개

---

## 1. User 엔티티 수정

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/user/entity/User.java`

**심각도:** ⚠️ 경미

### 문제점
- `email` 필드에 `unique = true` 제약조건 누락

### 수정 전
```java
@Column(name = "email", length = 256)
private String email;
```

### 수정 후
```java
@Column(name = "email", length = 256, unique = true, nullable = false)
private String email;
```

---

## 2. Wallet 엔티티 수정

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/wallet/entity/Wallet.java`

**심각도:** ⚠️ 보통

### 문제점
1. 컬럼명이 `update_at`인데 `updated_at`이어야 함
2. `columnDefinition = "BIGINT"`인데 필드 타입이 `Integer` (일관성 문제)
3. `cash`, `point`의 기본값 0 설정 누락

### 수정 전
```java
@Column(name = "cash", nullable = false, columnDefinition = "BIGINT")
private Integer cash;

@Column(name = "point", nullable = false, columnDefinition = "BIGINT")
private Integer point;

@LastModifiedDate
@Temporal(TemporalType.TIMESTAMP)
@Column(name = "update_at")
private LocalDateTime updateAt;
```

### 수정 후
```java
@Column(name = "cash", nullable = false, columnDefinition = "INT DEFAULT 0")
private Integer cash;

@Column(name = "point", nullable = false, columnDefinition = "INT DEFAULT 0")
private Integer point;

@LastModifiedDate
@Temporal(TemporalType.TIMESTAMP)
@Column(name = "updated_at")
private LocalDateTime updatedAt;
```

---

## 3. Seat 엔티티

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/seat/entity/Seat.java`

**심각도:** ✅ 정상

### 상태
- 모든 필드가 data-model.md 스펙과 일치
- SeatStatus가 `CONFIRMED`로 올바르게 변경됨
- 수정 불필요

---

## 4. Reservation 엔티티 수정

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/reservation/entity/Reservation.java`

**심각도:** ❌ 심각

### 문제점
1. `reservedAt`, `expiresAt`, `confirmedAt`의 타입이 `Long`인데 `LocalDateTime`이어야 함
2. `reservedAt`에 `@CreatedDate` 어노테이션 누락
3. 기본값 설정 누락

### 수정 전
```java
@Temporal(TemporalType.TIMESTAMP)
@Column(name = "reserved_at")
private Long reservedAt;

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "expires_at")
private Long expiresAt;

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "confirmed_at")
private Long confirmedAt;
```

### 수정 후
```java
@CreatedDate
@Temporal(TemporalType.TIMESTAMP)
@Column(name = "reserved_at", nullable = false, updatable = false)
private LocalDateTime reservedAt;

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "expires_at")
private LocalDateTime expiresAt;

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "confirmed_at")
private LocalDateTime confirmedAt;
```

### 추가 수정 사항
- Import 추가 필요: `import java.time.LocalDateTime;`
- `@CreatedDate` import 확인: `import org.springframework.data.annotation.CreatedDate;`

---

## 5. Payment 엔티티 수정

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/payment/entity/Payment.java`

**심각도:** ❌ 심각

### 문제점
1. `@Column` 어노테이션이 관계 필드(`Reservation reservation`)에 잘못 적용됨
2. `user_id` FK 관계 누락 (data-model.md 스펙에는 있음)

### 수정 전
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "reservation_id")
@Column(unique = true, nullable = false)
private Reservation reservation;
```

### 수정 후
```java
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "reservation_id", unique = true, nullable = false)
private Reservation reservation;

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;
```

### 추가 수정 사항
- Import 추가 필요: `import com.example.queue.seat_reservation.domain.user.entity.User;`

---

## 6. PaymentHistory 엔티티 수정

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/payment/entity/PaymentHistory.java`

**심각도:** ❌ 매우 심각

### 문제점
1. `type` 필드가 `PaymentHistory` 타입으로 선언됨 (재귀적 문제) → `HistoryType` enum이어야 함
2. `description`이 `Integer` 타입 → `String`이어야 함
3. `createdAt`이 `Long` 타입 → `LocalDateTime`이어야 함

### 수정 전
```java
@Column(name = "type", nullable = false)
private PaymentHistory paymentHistory;

@Column(name = "description", nullable = true)
private Integer description;

@CreatedDate
@Temporal(TemporalType.TIMESTAMP)
@Column(name = "created_at", updatable = false)
private Long createdAt;
```

### 수정 후
```java
@Enumerated(EnumType.STRING)
@Column(name = "type", nullable = false)
private HistoryType type;

@Column(name = "description")
private String description;

@CreatedDate
@Temporal(TemporalType.TIMESTAMP)
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;
```

### 추가 수정 사항
- Import 추가 필요: `import java.time.LocalDateTime;`
- HistoryType enum이 이미 존재하므로 import만 확인

---

## 7. TokenHistory 엔티티 수정

**파일:** `src/main/java/com/example/queue/seat_reservation/domain/token/entity/TokenHistory.java`

**심각도:** ❌ 심각

### 문제점
1. **필드명과 타입 불일치**: `userId`라는 이름인데 타입이 `User` (필드명을 `user`로 변경해야 함)
2. `status`가 `String` 타입 → `TokenStatus` enum이어야 함 (현재 코드에 여전히 String으로 되어 있음)
3. `@Enumerated` 어노테이션이 있지만 타입이 `String`이라 의미 없음

### 수정 전 (현재 코드)
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User userId;  // ❌ 필드명과 타입 불일치

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false)
private String status;  // ❌ TokenStatus여야 하는데 String

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "activated_at")
private LocalDateTime activatedAt;
```

### 수정 후
```java
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;  // ✅ 필드명 수정

@Enumerated(EnumType.STRING)
@Column(name = "status", nullable = false)
private TokenStatus status;  // ✅ TokenStatus enum 사용

@Temporal(TemporalType.TIMESTAMP)
@Column(name = "activated_at")
private LocalDateTime activatedAt;
```

### 추가 수정 사항
- TokenStatus enum이 이미 존재하므로 import만 확인

---

## 수정 우선순위

### 1순위 (즉시 수정 필요) - 컴파일 오류 및 심각한 논리 오류
- ✅ **PaymentHistory** - `type` 필드가 자기 자신 타입으로 선언됨 (재귀적 문제)
- ✅ **TokenHistory** - `userId`가 String이면서 @ManyToOne 선언

### 2순위 (비즈니스 로직 오류)
- ✅ **Reservation** - 시간 필드 타입 오류 (Long → LocalDateTime)
- ✅ **Payment** - user FK 관계 누락
- ✅ **PaymentHistory** - `description` 타입 오류, `createdAt` 타입 오류

### 3순위 (데이터 무결성)
- ⚠️ **User** - email unique 제약조건 누락
- ⚠️ **Wallet** - 컬럼명, 기본값 누락

---

## 수정 체크리스트

### User 엔티티
- [ ] email 필드에 `unique = true, nullable = false` 추가

### Wallet 엔티티
- [ ] cash, point 컬럼 정의 수정 (BIGINT → INT DEFAULT 0)
- [ ] updateAt → updatedAt 필드명 변경
- [ ] update_at → updated_at 컬럼명 변경

### Reservation 엔티티
- [ ] reservedAt 타입 변경 (Long → LocalDateTime)
- [ ] expiresAt 타입 변경 (Long → LocalDateTime)
- [ ] confirmedAt 타입 변경 (Long → LocalDateTime)
- [ ] reservedAt에 @CreatedDate 추가
- [ ] reservedAt에 nullable = false, updatable = false 추가

### Payment 엔티티
- [ ] reservation 필드의 @Column 제거
- [ ] user FK 관계 추가
- [ ] User import 추가

### PaymentHistory 엔티티
- [ ] type 필드 타입 변경 (PaymentHistory → HistoryType)
- [ ] type 필드에 @Enumerated(EnumType.STRING) 추가
- [ ] description 타입 변경 (Integer → String)
- [ ] createdAt 타입 변경 (Long → LocalDateTime)
- [ ] LocalDateTime import 추가

### TokenHistory 엔티티
- [ ] userId 필드명 변경 → user로 수정
- [ ] status 필드 타입 변경 (String → TokenStatus)

---

## 8. 인덱스 누락 (전체 엔티티)

**심각도:** ❌ 심각

### 문제점
**모든 엔티티에 data-model.md에 명시된 인덱스가 정의되지 않았습니다.**

JPA에서는 `@Table(indexes = {...})` 어노테이션으로 인덱스를 정의해야 합니다.

---

### 8.1 User 엔티티 인덱스 추가

```java
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true)
})
```

**체크리스트:**
- [ ] idx_user_email 인덱스 추가 (email, UNIQUE)

---

### 8.2 Wallet 엔티티 인덱스 추가

```java
@Table(name = "wallets", indexes = {
    @Index(name = "idx_wallet_user", columnList = "user_id", unique = true)
})
```

**체크리스트:**
- [ ] idx_wallet_user 인덱스 추가 (user_id, UNIQUE)

---

### 8.3 Seat 엔티티 인덱스 추가

```java
@Table(name = "seats", indexes = {
    @Index(name = "idx_seat_status", columnList = "status"),
    @Index(name = "idx_seat_updated_at", columnList = "updated_at")
})
```

**체크리스트:**
- [ ] idx_seat_status 인덱스 추가 (status)
- [ ] idx_seat_updated_at 인덱스 추가 (updated_at)

---

### 8.4 Reservation 엔티티 인덱스 추가

```java
@Table(name = "reservations", indexes = {
    @Index(name = "idx_reservation_user", columnList = "user_id"),
    @Index(name = "idx_reservation_seat", columnList = "seat_id"),
    @Index(name = "idx_reservation_status", columnList = "status"),
    @Index(name = "idx_reservation_expires", columnList = "expires_at"),
    @Index(name = "idx_user_status", columnList = "user_id, status")
})
```

**체크리스트:**
- [ ] idx_reservation_user 인덱스 추가 (user_id)
- [ ] idx_reservation_seat 인덱스 추가 (seat_id)
- [ ] idx_reservation_status 인덱스 추가 (status)
- [ ] idx_reservation_expires 인덱스 추가 (expires_at)
- [ ] idx_user_status 복합 인덱스 추가 (user_id, status)

**특별 인덱스 (부분 인덱스):**
```sql
-- JPA로 직접 정의 불가, Flyway/Liquibase 마이그레이션으로 생성
CREATE UNIQUE INDEX idx_active_reservation
ON reservations(seat_id)
WHERE status IN ('TEMP_RESERVED', 'CONFIRMED');
```
- [ ] idx_active_reservation 부분 인덱스 추가 (마이그레이션 스크립트)

---

### 8.5 Payment 엔티티 인덱스 추가

```java
@Table(name = "payments", indexes = {
    @Index(name = "idx_payment_reservation", columnList = "reservation_id", unique = true),
    @Index(name = "idx_payment_user", columnList = "user_id"),
    @Index(name = "idx_payment_paid_at", columnList = "paid_at")
})
```

**체크리스트:**
- [ ] idx_payment_reservation 인덱스 추가 (reservation_id, UNIQUE)
- [ ] idx_payment_user 인덱스 추가 (user_id)
- [ ] idx_payment_paid_at 인덱스 추가 (paid_at)

---

### 8.6 PaymentHistory 엔티티 인덱스 추가

```java
@Table(name = "payment_histories", indexes = {
    @Index(name = "idx_history_wallet", columnList = "wallet_id"),
    @Index(name = "idx_history_created", columnList = "created_at"),
    @Index(name = "idx_history_type", columnList = "type"),
    @Index(name = "idx_wallet_type", columnList = "wallet_id, type")
})
```

**체크리스트:**
- [ ] idx_history_wallet 인덱스 추가 (wallet_id)
- [ ] idx_history_created 인덱스 추가 (created_at)
- [ ] idx_history_type 인덱스 추가 (type)
- [ ] idx_wallet_type 복합 인덱스 추가 (wallet_id, type)

---

### 8.7 TokenHistory 엔티티 인덱스 추가

```java
@Table(name = "token_histories", indexes = {
    @Index(name = "idx_token_user", columnList = "user_id"),
    @Index(name = "idx_token_status", columnList = "status"),
    @Index(name = "idx_token_created", columnList = "created_at"),
    @Index(name = "idx_token_expired", columnList = "expired_at")
})
```

**체크리스트:**
- [ ] idx_token_user 인덱스 추가 (user_id)
- [ ] idx_token_status 인덱스 추가 (status)
- [ ] idx_token_created 인덱스 추가 (created_at)
- [ ] idx_token_expired 인덱스 추가 (expired_at)

---

## 9. CHECK 제약조건 누락

**심각도:** ⚠️ 보통

### 문제점
data-model.md에 정의된 CHECK 제약조건들이 엔티티에 없습니다.

**참고:** JPA 표준은 `@Check` 어노테이션을 지원하지 않습니다. 따라서:
1. **권장 방법**: Bean Validation 사용 (`@Min`, `@Positive`, `@PositiveOrZero`)
2. **대안 1**: 애플리케이션 레벨에서 비즈니스 로직으로 검증
3. **대안 2**: Flyway/Liquibase 마이그레이션 스크립트에서 CHECK 제약조건 추가

---

### 9.1 Wallet 엔티티 CHECK 제약조건

**필요한 제약조건:**
- `cash >= 0`
- `point >= 0`

**Bean Validation 적용:**
```java
@Column(name = "cash", nullable = false)
@PositiveOrZero(message = "현금은 0 이상이어야 합니다.")
private Integer cash;

@Column(name = "point", nullable = false)
@PositiveOrZero(message = "포인트는 0 이상이어야 합니다.")
private Integer point;
```

**체크리스트:**
- [ ] cash 필드에 `@PositiveOrZero` 추가
- [ ] point 필드에 `@PositiveOrZero` 추가
- [ ] `import jakarta.validation.constraints.PositiveOrZero;` 추가

---

### 9.2 Seat 엔티티 CHECK 제약조건

**필요한 제약조건:**
- `price > 0`

**Bean Validation 적용:**
```java
@Column(name = "price", nullable = false)
@Positive(message = "가격은 양수여야 합니다.")
private Integer price;
```

**체크리스트:**
- [ ] price 필드에 `@Positive` 추가
- [ ] `import jakarta.validation.constraints.Positive;` 추가

---

### 9.3 Reservation 엔티티 CHECK 제약조건

**필요한 제약조건:**
- `price > 0`
- `status = TEMP_RESERVED`일 때 `expires_at NOT NULL`
- `status = CONFIRMED`일 때 `confirmed_at NOT NULL`

**Bean Validation 적용:**
```java
@Column(name = "price", nullable = false)
@Positive(message = "가격은 양수여야 합니다.")
private Integer price;
```

**비즈니스 로직 검증:**
```java
// Service 레이어에서 검증
if (status == ReservationStatus.TEMP_RESERVED && expiresAt == null) {
    throw new IllegalStateException("임시 예약은 만료 시간이 필요합니다.");
}
if (status == ReservationStatus.CONFIRMED && confirmedAt == null) {
    throw new IllegalStateException("확정 예약은 확정 시간이 필요합니다.");
}
```

**체크리스트:**
- [ ] price 필드에 `@Positive` 추가
- [ ] Service 레이어에 비즈니스 로직 검증 추가

---

### 9.4 Payment 엔티티 CHECK 제약조건

**필요한 제약조건:**
- `total_amount > 0`
- `point_used >= 0`
- `cash_used >= 0`
- `point_earned >= 0`
- `total_amount = point_used + cash_used`

**Bean Validation 적용:**
```java
@Column(name = "total_amount", nullable = false)
@Positive(message = "총 결제 금액은 양수여야 합니다.")
private Integer totalAmount;

@Column(name = "point_used", nullable = false)
@PositiveOrZero(message = "사용 포인트는 0 이상이어야 합니다.")
private Integer pointUsed;

@Column(name = "cash_used", nullable = false)
@PositiveOrZero(message = "사용 현금은 0 이상이어야 합니다.")
private Integer cashUsed;

@Column(name = "point_earned", nullable = false)
@PositiveOrZero(message = "적립 포인트는 0 이상이어야 합니다.")
private Integer pointEarned;
```

**비즈니스 로직 검증:**
```java
// Service 레이어에서 검증
if (totalAmount != pointUsed + cashUsed) {
    throw new IllegalStateException("총 금액이 포인트 + 현금과 일치하지 않습니다.");
}
```

**체크리스트:**
- [ ] totalAmount 필드에 `@Positive` 추가
- [ ] pointUsed 필드에 `@PositiveOrZero` 추가
- [ ] cashUsed 필드에 `@PositiveOrZero` 추가
- [ ] pointEarned 필드에 `@PositiveOrZero` 추가
- [ ] Service 레이어에 금액 일치 검증 추가

---

## 10. 인덱스 체크리스트 요약

### 총 인덱스 개수: 21개
- User: 1개
- Wallet: 1개
- Seat: 2개
- Reservation: 6개 (부분 인덱스 1개 포함)
- Payment: 3개
- PaymentHistory: 4개
- TokenHistory: 4개

### 복합 인덱스: 3개
- Reservation: (user_id, status)
- PaymentHistory: (wallet_id, type)

### 부분 인덱스: 1개
- Reservation: (seat_id) WHERE status IN ('TEMP_RESERVED', 'CONFIRMED')

---

## 수정 후 검증 사항

### 1. 컴파일 검증
```bash
./gradlew clean build
```

### 2. JPA DDL 생성 검증
```yaml
# application.yml에 추가하여 DDL 확인
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # 또는 create
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### 3. 테이블 생성 확인
- H2 Console에서 각 테이블 스키마 확인
- 외래 키 제약조건 확인
- UNIQUE 제약조건 확인

### 4. 관계 매핑 테스트
- User ↔ Wallet (1:1)
- User ↔ Reservation (1:N)
- Seat ↔ Reservation (1:N)
- Reservation ↔ Payment (1:1)
- Wallet ↔ PaymentHistory (1:N)
- User ↔ TokenHistory (1:N)

---

## 참고: data-model.md 스펙 링크

각 엔티티의 정확한 스펙은 `docs/data-model.md`의 다음 섹션 참조:
- User: 2.1절
- Wallet: 2.2절
- Seat: 2.3절
- Reservation: 2.4절
- Payment: 2.5절
- PaymentHistory: 2.6절
- TokenHistory: 2.7절

---

## 작성일
2025-11-03

## 업데이트 이력
- **v1.0.0** (2025-11-03): 초기 작성 - 필드 타입 및 매핑 오류 16개
- **v2.0.0** (2025-11-03): 인덱스 및 제약조건 추가 - 총 40개 이상 문제점 정리

## 버전
2.0.0
