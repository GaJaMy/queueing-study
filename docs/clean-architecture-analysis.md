# 프로젝트 클린 아키텍처 분석

> **작성일**: 2025-11-04
> **버전**: 1.0.0
> **목적**: 현재 프로젝트의 클린 아키텍처 준수 여부 분석 및 개선 방안 제시

---

## 📋 목차

1. [프로젝트 구조 설명](#1-프로젝트-구조-설명)
2. [현재 아키텍처 분석](#2-현재-아키텍처-분석)
3. [잘된 점 (Clean Architecture 부합)](#3-잘된-점-clean-architecture-부합)
4. [문제점 (Clean Architecture 위반)](#4-문제점-clean-architecture-위반)
5. [해결 방안](#5-해결-방안)
6. [최종 평가](#6-최종-평가)
7. [개선 계획](#7-개선-계획)

---

## 1. 프로젝트 구조 설명

### 1.1 레이어별 역할

#### 📱 `interface/`
**역할**: 외부 세계와 만나는 인터페이스
**포함**:
- `*/controller/`: REST API Controller
- `*/swagger/`: API 문서화

#### 💼 `application/`
**역할**: 비즈니스 로직이 존재하는 곳
**포함**:
- `*/service/`: 각 도메인별 **핵심 비즈니스 로직** (재사용 가능한 단위 기능)
- `*/usecase/`: Service를 조합한 **완전한 비즈니스 기능** (하나의 요구사항)
- `*/dto/`: 외부 세계와 데이터를 주고받기 위한 객체 (요청/응답)
- `*/adaptor/`: Infrastructure와의 인터페이스 (Port 역할)

#### 🏛️ `domain/`
**역할**: 도메인 객체들의 정의
**포함**:
- `*/entity/`: JPA Entity (User, Wallet, Seat, Reservation, Payment, Token 등)

#### 🔧 `infrastructure/`
**역할**: DB와 같은 기술적 구현
**포함**:
- `*/adaptor/`: application의 adaptor 인터페이스 구현체
- `*/repository/`: JPA Repository
- `exception/`: 예외 처리
- `config/`: 설정

---

## 2. 현재 아키텍처 분석

### 2.1 디렉토리 구조

```
com.example.queue.seat_reservation/
│
├── interface/                           # Presentation Layer
│   ├── queue/
│   │   ├── controller/QueueController
│   │   └── swagger/
│   └── seat/
│       ├── controller/SeatController
│       └── swagger/
│
├── application/                         # Application Layer
│   ├── user/
│   │   ├── service/UserService         # 도메인별 핵심 비즈니스 로직
│   │   ├── adaptor/UserAdaptor         # Port 인터페이스
│   │   ├── command/
│   │   └── mapper/
│   ├── wallet/
│   │   ├── service/WalletService
│   │   └── adaptor/WalletAdaptor
│   ├── seat/
│   │   ├── service/SeatService
│   │   └── adaptor/SeatAdaptor
│   ├── payment/
│   │   ├── service/PaymentService
│   │   └── adaptor/PaymentAdaptor
│   ├── token/
│   │   └── service/TokenService
│   └── queue/
│       ├── usecase/QueueUseCase        # Service를 조합한 완전한 기능
│       └── dto/
│           ├── request/
│           └── response/
│
├── domain/                              # Domain Layer
│   ├── user/entity/User
│   ├── wallet/entity/Wallet
│   ├── seat/entity/Seat
│   ├── reservation/entity/Reservation
│   ├── payment/entity/
│   │   ├── Payment
│   │   ├── PaymentHistory
│   │   └── HistoryType
│   └── token/entity/
│       ├── TokenHistory
│       └── TokenStatus
│
└── infrastructure/                      # Infrastructure Layer
    ├── user/
    │   ├── adaptor/UserJpaAdaptor      # Adaptor 구현체
    │   └── repository/UserRepository   # JPA Repository
    ├── wallet/
    │   ├── adaptor/WalletJpaAdaptor
    │   └── repository/WalletRepository
    ├── seat/
    │   ├── adaptor/SeatJpaAdaptor
    │   └── repository/SeatRepository
    ├── payment/
    │   ├── adaptor/PaymentJpaAdaptor
    │   └── repository/PaymentRepository
    ├── exception/
    │   ├── CustomException.java        ❌ 문제!
    │   ├── ErrorCode.java              ❌ 문제!
    │   └── GlobalExceptionController
    └── config/
```

### 2.2 의존성 흐름

```
┌──────────────────────────────────────┐
│   Interface (Controller)             │
└──────────────┬───────────────────────┘
               │ depends on
               ↓
┌──────────────────────────────────────┐
│   Application (Service, UseCase)     │
│   ├── Service (단위 기능)            │
│   └── UseCase (완전한 요구사항)       │
└──────────────┬───────────────────────┘
               │ depends on
               ↓
┌──────────────────────────────────────┐
│   Domain (Entity)                    │
└──────────────▲───────────────────────┘
               │ implements
               │
┌──────────────┴───────────────────────┐
│   Infrastructure (Adaptor, Repo)    │
└──────────────────────────────────────┘
```

---

## 3. 잘된 점 (Clean Architecture 부합)

### 3.1 ✅ 명확한 레이어 분리

프로젝트는 4개의 명확한 레이어로 구성되어 있습니다:

```
Interface → Application → Domain ← Infrastructure
```

**장점**:
- 각 레이어의 책임이 명확함
- 레이어 간 경계가 잘 정의됨
- 변경의 영향 범위를 제한할 수 있음

---

### 3.2 ✅ Service와 UseCase 분리 (매우 우수!)

이 설계는 **모범 사례**입니다!

#### Service: 도메인별 핵심 비즈니스 로직
```java
// application/user/service/UserService.java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;

    // 재사용 가능한 단위 기능
    public User getUser(String userId) {
        return userAdaptor.getUser(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
    }

    public void registerUser(User user) {
        userAdaptor.saveUser(user);
    }
}
```

#### UseCase: Service를 조합한 완전한 비즈니스 기능
```java
// application/queue/usecase/QueueUseCase.java
@Service
@RequiredArgsConstructor
public class QueueUseCase {
    private final TemporaryRepositoryService temporaryRepositoryService;
    // 필요한 여러 Service 주입

    // 하나의 완전한 요구사항 (여러 Service 조합)
    public IssueQueueResponseDto issueQueueToken(IssueQueueRequestDto dto) {
        // 1. 사용자 검증
        // 2. 토큰 생성
        // 3. 대기열 추가
        // 4. 알림 전송
        return null;
    }

    public GetQueuePositionResponseDto getQueuePosition(String token) {
        // 대기열 순서 조회 로직
        return null;
    }
}
```

**왜 이 설계가 우수한가?**

| 측면 | Service | UseCase |
|------|---------|---------|
| **책임** | 단일 도메인의 핵심 기능 | 여러 도메인을 조합한 완전한 기능 |
| **재사용성** | ✅ 높음 (다른 UseCase에서 재사용) | ⚠️ 낮음 (특정 요구사항 전용) |
| **복잡도** | 낮음 (단순) | 높음 (복잡한 비즈니스 로직) |
| **테스트** | 단위 테스트 쉬움 | 통합 테스트 필요 |
| **예시** | `getUser()`, `saveWallet()` | `issueQueueToken()`, `processPayment()` |

**장점**:
- ✅ Controller는 UseCase만 호출하면 됨 (단순함)
- ✅ Service는 재사용 가능한 빌딩 블록
- ✅ 비즈니스 로직의 복잡도를 UseCase에서 관리
- ✅ CQRS 패턴과 잘 맞음
- ✅ 테스트 전략이 명확함 (Service = 단위, UseCase = 통합)

---

### 3.3 ✅ Port & Adapter 패턴 (의존성 역전)

```
UserAdaptor (인터페이스, application)
    ↓ implements
UserJpaAdaptor (구현체, infrastructure)
    → UserRepository (JPA)
```

**코드 예시**:
```java
// application/user/adaptor/UserAdaptor.java (Port)
public interface UserAdaptor {
    Optional<User> getUser(String userId);
    void saveUser(User user);
}

// infrastructure/user/adaptor/UserJpaAdaptor.java (Adapter)
@Repository
@RequiredArgsConstructor
public class UserJpaAdaptor implements UserAdaptor {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUser(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }
}
```

**장점**:
- ✅ 의존성 역전 원칙(DIP) 준수
- ✅ Application이 Infrastructure를 직접 의존하지 않음
- ✅ 테스트 시 Adaptor를 Mock으로 쉽게 대체 가능
- ✅ Infrastructure 기술 변경 시 Adapter만 수정하면 됨

---

### 3.4 ✅ DTO 분리

```java
// application/queue/dto/request/IssueQueueRequestDto.java
public class IssueQueueRequestDto {
    private String userId;
    // ...
}

// application/queue/dto/response/IssueQueueResponseDto.java
public class IssueQueueResponseDto {
    private String token;
    private int position;
    // ...
}
```

**장점**:
- ✅ Entity와 API 응답 분리
- ✅ Entity 변경이 API에 영향 없음
- ✅ 보안 (Entity의 민감한 필드 노출 방지)

---

## 4. 문제점 (Clean Architecture 위반)

### 🚨 Critical: Application이 Infrastructure에 의존

#### 현재 문제 코드

```java
// application/user/service/UserService.java
package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.infrastructure.exception.CustomException;  // ❌
import com.example.queue.seat_reservation.infrastructure.exception.ErrorCode;       // ❌

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;

    public User getUser(String userId) {
        return userAdaptor.getUser(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_EXIST_USER));
    }
}
```

#### 왜 문제인가?

**클린 아키텍처의 의존성 규칙**:
> 소스 코드 의존성은 항상 외부에서 내부로, 저수준에서 고수준으로 향해야 한다.

**현재 의존성 방향** (잘못됨):
```
Application ──→ Infrastructure (exception)
     ↓
   Domain
```

**올바른 의존성 방향**:
```
Infrastructure ──→ Application ──→ Domain
```

#### 문제점
- ❌ **의존성 방향 위반**: 내부 레이어(Application)가 외부 레이어(Infrastructure)에 의존
- ❌ **클린 아키텍처 핵심 원칙 위반**: 비즈니스 로직이 기술 세부사항에 의존
- ❌ **테스트 어려움**: Infrastructure 없이 Application 테스트 불가
- ❌ **유연성 저하**: Infrastructure 변경 시 Application도 변경 필요

---

## 5. 해결 방안

### 5.1 Exception 위치 재구성

#### Before (현재)
```
infrastructure/exception/
  ├── CustomException.java    ❌ Application에서 사용하므로 위치 부적절
  └── ErrorCode.java           ❌ Application에서 사용하므로 위치 부적절
```

#### After (개선)
```
application/exception/
  └── ErrorCode.java           ✅ Application 레벨 에러 코드 정의

domain/exception/
  ├── DomainException.java     ✅ Base 예외 클래스
  ├── NotFoundException.java   ✅ 공통 예외
  └── AlreadyExistsException.java ✅ 공통 예외

infrastructure/exception/
  └── GlobalExceptionHandler.java  ✅ 예외 처리만 담당
```

---

### 5.2 개선된 코드

#### 1) ErrorCode (Application Layer)

```java
// application/exception/ErrorCode.java
package com.example.queue.seat_reservation.application.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // 404 Not Found
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "US000", "존재하지 않는 유저입니다."),
    NOT_EXIST_WALLET(HttpStatus.NOT_FOUND, "WL000", "존재하지 않는 지갑입니다."),
    NOT_EXIST_SEAT(HttpStatus.NOT_FOUND, "SE000", "존재하지 않는 좌석입니다."),

    // 400 Bad Request
    INSUFFICIENT_BALANCE(HttpStatus.BAD_REQUEST, "WL001", "잔액이 부족합니다."),
    SEAT_ALREADY_RESERVED(HttpStatus.BAD_REQUEST, "SE001", "이미 예약된 좌석입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "TK001", "유효하지 않은 토큰입니다."),

    // 500 Internal Server Error
    SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "SY001", "서버 에러");

    private final HttpStatus status;
    private final String code;
    private final String msg;

    ErrorCode(HttpStatus status, String code, String msg) {
        this.status = status;
        this.code = code;
        this.msg = msg;
    }
}
```

#### 2) DomainException (Domain Layer)

```java
// domain/exception/DomainException.java
package com.example.queue.seat_reservation.domain.exception;

import com.example.queue.seat_reservation.application.exception.ErrorCode;
import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    protected DomainException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.errorCode = errorCode;
    }

    protected DomainException(ErrorCode errorCode, String detail) {
        super(errorCode.getMsg() + ": " + detail);
        this.errorCode = errorCode;
    }
}
```

#### 3) NotFoundException (Domain Layer)

```java
// domain/exception/NotFoundException.java
package com.example.queue.seat_reservation.domain.exception;

import com.example.queue.seat_reservation.application.exception.ErrorCode;

public class NotFoundException extends DomainException {
    public NotFoundException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotFoundException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
```

#### 4) AlreadyExistsException (Domain Layer)

```java
// domain/exception/AlreadyExistsException.java
package com.example.queue.seat_reservation.domain.exception;

import com.example.queue.seat_reservation.application.exception.ErrorCode;

public class AlreadyExistsException extends DomainException {
    public AlreadyExistsException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AlreadyExistsException(ErrorCode errorCode, String detail) {
        super(errorCode, detail);
    }
}
```

#### 5) 개선된 Service

```java
// application/user/service/UserService.java
package com.example.queue.seat_reservation.application.user.service;

import com.example.queue.seat_reservation.application.user.adaptor.UserAdaptor;
import com.example.queue.seat_reservation.domain.user.entity.User;
import com.example.queue.seat_reservation.domain.exception.NotFoundException;  // ✅
import com.example.queue.seat_reservation.application.exception.ErrorCode;    // ✅
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;

    public void registerUser(User user) {
        userAdaptor.saveUser(user);
    }

    public User getUser(String userId) {
        return userAdaptor.getUser(userId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.NOT_EXIST_USER));
    }
}
```

#### 6) GlobalExceptionHandler (Infrastructure Layer)

```java
// infrastructure/exception/GlobalExceptionHandler.java
package com.example.queue.seat_reservation.infrastructure.exception;

import com.example.queue.seat_reservation.domain.exception.DomainException;
import com.example.queue.seat_reservation.domain.exception.NotFoundException;
import com.example.queue.seat_reservation.domain.exception.AlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException: code={}, message={}",
                  e.getErrorCode().getCode(),
                  e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(
                    e.getErrorCode().getCode(),
                    e.getErrorCode().getMsg()
                ));
    }

    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExistsException(AlreadyExistsException e) {
        log.error("AlreadyExistsException: code={}, message={}",
                  e.getErrorCode().getCode(),
                  e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(
                    e.getErrorCode().getCode(),
                    e.getErrorCode().getMsg()
                ));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException e) {
        log.error("DomainException: code={}, message={}",
                  e.getErrorCode().getCode(),
                  e.getMessage());

        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(new ErrorResponse(
                    e.getErrorCode().getCode(),
                    e.getErrorCode().getMsg()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected error", e);

        return ResponseEntity
                .status(500)
                .body(new ErrorResponse("SY001", "서버 에러"));
    }
}
```

#### 7) ErrorResponse

```java
// infrastructure/exception/ErrorResponse.java
package com.example.queue.seat_reservation.infrastructure.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private String code;
    private String message;
}
```

---

### 5.3 개선 후 의존성 방향

```
┌─────────────────────────────────────┐
│   Infrastructure Layer              │
│   - GlobalExceptionHandler          │  ← Domain Exception 처리
│   - ErrorResponse                   │
└──────────────┬──────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────┐
│   Application Layer                 │
│   - Service                          │
│   - UseCase                          │
│   - ErrorCode (Enum)                │  ← 에러 코드 정의
└──────────────┬──────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────┐
│   Domain Layer                      │
│   - Entity                           │
│   - DomainException (Base)          │  ← 예외 클래스
│   - NotFoundException               │
│   - AlreadyExistsException          │
└─────────────────────────────────────┘
```

**✅ 올바른 의존성 방향 달성!**

---

## 6. 최종 평가

### 6.1 클린 아키텍처 준수도

| 항목 | 평가 | 점수 | 비고 |
|------|------|------|------|
| **레이어 분리** | ✅ 매우 우수 | 10/10 | 명확한 4-레이어 구조 |
| **Service/UseCase 분리** | ✅ 탁월함 | 10/10 | 모범 사례 |
| **Port & Adapter 패턴** | ✅ 잘 구현됨 | 9/10 | 의존성 역전 원칙 준수 |
| **의존성 방향** | ❌ Exception 위반 | 6/10 | **개선 필요** |
| **Entity 위치** | ✅ 적절함 | 9/10 | Domain Layer에 위치 |
| **DTO 분리** | ✅ 잘됨 | 9/10 | Entity와 분리 |
| **테스트 용이성** | ✅ 좋음 | 8/10 | Adaptor로 Mock 가능 |

### 6.2 종합 점수

**8.8 / 10** ⭐⭐⭐⭐

### 6.3 총평

**✅ 전체적으로 클린 아키텍처를 매우 잘 따르고 있음**

**강점**:
- ✅ Service와 UseCase 분리는 **업계 모범 사례**
- ✅ Port & Adapter 패턴을 통한 의존성 역전 구현
- ✅ 명확한 레이어 분리와 책임 분리

**개선 필요**:
- ⚠️ Exception 위치만 수정하면 **완벽한 클린 아키텍처** 달성

---

## 7. 개선 계획

### 7.1 우선순위 1: Exception 재구성 (예상 시간: 30분)

#### 1단계: 새 파일 생성

```bash
# 1. ErrorCode를 Application으로 이동
touch src/main/java/com/example/queue/seat_reservation/application/exception/ErrorCode.java

# 2. Domain Exception 생성
mkdir -p src/main/java/com/example/queue/seat_reservation/domain/exception
touch src/main/java/com/example/queue/seat_reservation/domain/exception/DomainException.java
touch src/main/java/com/example/queue/seat_reservation/domain/exception/NotFoundException.java
touch src/main/java/com/example/queue/seat_reservation/domain/exception/AlreadyExistsException.java
```

#### 2단계: 코드 작성
위의 [5.2 개선된 코드](#52-개선된-코드) 참고

#### 3단계: Service 클래스 수정
```bash
# 모든 Service 클래스에서 import 변경
# Before: import com.example...infrastructure.exception.CustomException;
# After:  import com.example...domain.exception.NotFoundException;
```

**수정 대상 파일**:
- `application/user/service/UserService.java`
- `application/wallet/service/WalletService.java`
- `application/seat/service/SeatService.java`
- `application/payment/service/PaymentService.java`
- `application/token/service/TokenService.java`

#### 4단계: 기존 파일 삭제
```bash
# Infrastructure의 예외 파일 삭제
rm src/main/java/com/example/queue/seat_reservation/infrastructure/exception/CustomException.java
rm src/main/java/com/example/queue/seat_reservation/infrastructure/exception/ErrorCode.java

# GlobalExceptionHandler는 유지하되, 내용 수정
```

---

### 7.2 우선순위 2: GlobalExceptionHandler 개선 (예상 시간: 10분)

#### 개선 내용
- `DomainException`을 처리하도록 수정
- `ErrorResponse` 클래스 추가
- 로깅 개선

위의 [5.2 개선된 코드 - 6) GlobalExceptionHandler](#52-개선된-코드) 참고

---

### 7.3 검증 체크리스트

#### ✅ 의존성 방향 검증
```bash
# Application에서 Infrastructure import가 없는지 확인
grep -r "import.*infrastructure" src/main/java/*/application/ | grep -v "test"

# 결과: 아무것도 나오지 않아야 함
```

#### ✅ Exception 위치 검증
```bash
# ErrorCode가 Application에 있는지 확인
ls src/main/java/com/example/queue/seat_reservation/application/exception/ErrorCode.java

# Domain Exception이 존재하는지 확인
ls src/main/java/com/example/queue/seat_reservation/domain/exception/

# Infrastructure에 CustomException이 없는지 확인
! ls src/main/java/com/example/queue/seat_reservation/infrastructure/exception/CustomException.java
```

#### ✅ 빌드 확인
```bash
./gradlew clean build

# 결과: BUILD SUCCESSFUL
```

#### ✅ 테스트 실행
```bash
./gradlew test

# 모든 테스트 PASS 확인
```

---

## 8. 개선 후 최종 구조

### 8.1 Exception 패키지 구조

```
com.example.queue.seat_reservation/
│
├── application/
│   └── exception/
│       └── ErrorCode.java           ✅ 에러 코드 정의 (Application 레벨)
│
├── domain/
│   └── exception/
│       ├── DomainException.java     ✅ Base 예외 클래스
│       ├── NotFoundException.java   ✅ 공통 예외
│       └── AlreadyExistsException.java ✅ 공통 예외
│
└── infrastructure/
    └── exception/
        ├── GlobalExceptionHandler.java  ✅ 예외 처리 핸들러
        └── ErrorResponse.java           ✅ API 응답 DTO
```

### 8.2 의존성 방향 (개선 완료)

```
┌─────────────────────────────────────┐
│   Infrastructure                    │
│   - GlobalExceptionHandler          │
└──────────────┬──────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────┐
│   Application                       │
│   - Service, UseCase                │
│   - ErrorCode                       │
└──────────────┬──────────────────────┘
               │ depends on
               ↓
┌─────────────────────────────────────┐
│   Domain                            │
│   - Entity                          │
│   - DomainException                 │
└─────────────────────────────────────┘

✅ 클린 아키텍처 의존성 규칙 100% 준수!
```

---

## 9. FAQ

### Q1: Service와 UseCase를 꼭 분리해야 하나요?

**A**: 필수는 아니지만, **강력히 권장합니다**.

**장점**:
- Service는 재사용 가능한 빌딩 블록 역할
- UseCase는 하나의 요구사항을 명확히 표현
- Controller가 단순해짐 (UseCase만 호출)
- 테스트 전략이 명확해짐

**언제 분리?**
- ✅ 여러 도메인이 협력하는 경우
- ✅ 복잡한 비즈니스 플로우가 있는 경우
- ❌ 단순 CRUD만 있는 경우는 Service만으로 충분

---

### Q2: Port와 Adapter 대신 Repository를 직접 사용하면 안 되나요?

**A**: 가능하지만, 권장하지 않습니다.

**현재 구조 (Port & Adapter)**:
```java
@Service
public class UserService {
    private final UserAdaptor userAdaptor;  // 인터페이스에 의존
}
```

**직접 Repository 사용**:
```java
@Service
public class UserService {
    private final UserRepository userRepository;  // Infrastructure에 직접 의존 ❌
}
```

**Port & Adapter의 장점**:
- ✅ 의존성 역전 원칙 준수
- ✅ Application이 Infrastructure를 모름
- ✅ 테스트 시 Mock 생성 쉬움
- ✅ Infrastructure 기술 변경 시 영향 최소화

---

### Q3: ErrorCode를 Application에 두는 이유는?

**A**: ErrorCode는 **비즈니스 규칙**이기 때문입니다.

**ErrorCode의 성격**:
- "사용자가 존재하지 않음" → 비즈니스 규칙
- "잔액이 부족함" → 비즈니스 규칙
- "좌석이 이미 예약됨" → 비즈니스 규칙

**따라서**:
- ❌ Infrastructure에 두면: 기술 세부사항으로 취급
- ✅ Application에 두면: 비즈니스 규칙으로 취급

**레이어별 역할**:
- `application/exception/ErrorCode`: 에러 코드 정의 (비즈니스 규칙)
- `domain/exception/DomainException`: 예외 클래스 (도메인 개념)
- `infrastructure/exception/GlobalExceptionHandler`: HTTP 응답 처리 (기술 구현)

---

### Q4: 개선 후에도 Adaptor 레이어가 필요한가요?

**A**: 네, **필요합니다**.

Adaptor는 **Port & Adapter 패턴의 핵심**입니다:

```
Application (Port 정의)
    ↓ depends on
Infrastructure (Adapter 구현)
```

**Adaptor가 없으면**:
- Application이 Infrastructure(Repository)를 직접 의존
- 의존성 역전 원칙 위반
- 테스트 어려움

**Adaptor 유지 이유**:
- ✅ 의존성 방향 제어
- ✅ 기술 변경 용이 (JPA → MongoDB 등)
- ✅ 테스트 용이성

---

## 10. 결론

### 10.1 현재 상태

**당신의 프로젝트는 클린 아키텍처를 거의 완벽하게 따르고 있습니다!**

특히 **Service와 UseCase를 분리한 설계**는 매우 우수하며, 많은 프로젝트에서 참고할 만한 구조입니다.

### 10.2 개선 후 기대 효과

**Exception 위치만 수정하면**:
- ✅ 클린 아키텍처의 의존성 규칙 100% 준수
- ✅ Application Layer의 독립성 확보
- ✅ 테스트 용이성 향상
- ✅ 유지보수성 향상

### 10.3 다음 단계

1. **Exception 재구성 완료** (이 문서의 개선 계획 참고)
2. 비즈니스 로직 구현 (UseCase 완성)
3. 테스트 코드 작성
4. API 문서화 (Swagger)

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|----------|
| 1.0.0 | 2025-11-04 | 초기 문서 작성 |

