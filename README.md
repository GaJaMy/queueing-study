# 🎫 콘서트 좌석 예약 시스템

대기열 기반의 공정한 선착순 콘서트 좌석 예약 시스템입니다. Spring Boot와 Clean Architecture를 기반으로 구축되었으며, 높은 동시성 처리와 데이터 무결성을 보장합니다.

## 📋 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [시작하기](#-시작하기)
- [API 문서](#-api-문서)
- [데이터베이스](#-데이터베이스)
- [테스트](#-테스트)
- [문서](#-문서)

## 🎯 프로젝트 소개

이 시스템은 콘서트 좌석 예약 시 발생하는 **트래픽 폭주**와 **동시성 문제**를 해결하기 위해 설계되었습니다.

### 핵심 특징

- **대기열 시스템**: FIFO 방식의 공정한 선착순 처리
- **동시성 제어**: 비관적 락을 통한 좌석 이중 예약 방지
- **Clean Architecture**: 의존성 역전 원칙과 Port/Adaptor 패턴 적용
- **확장 가능한 설계**: Service/UseCase 분리를 통한 재사용성 극대화

## ✨ 주요 기능

### 1. 대기열 관리
- **토큰 발급**: UUID 기반의 고유한 대기열 토큰 생성
- **실시간 대기 순번 조회**: 현재 위치와 예상 대기 시간 제공
- **자동 입장 처리**: 10초마다 10명씩 대기열에서 활성 상태로 전환
- **토큰 만료 관리**:
  - WAITING 상태: 무한 대기 (만료 없음)
  - ACTIVE 상태: 30분 자동 만료

### 2. 좌석 예약
- **실시간 좌석 조회**: 예약 가능한 좌석 목록 확인
- **임시 예약**: 5분간 좌석 점유
- **동시성 제어**: SELECT FOR UPDATE를 통한 비관적 락 적용
- **자동 만료**: 5분 내 미결제 시 좌석 자동 해제

### 3. 결제 시스템
- **복합 결제**: 포인트 + 현금 동시 사용 가능
- **포인트 적립**: 현금 결제액의 5% 자동 적립
- **트랜잭션 보장**: 원자성 있는 결제 처리
- **결제 내역 관리**: 모든 거래 이력 추적 (cash_after, point_after)

### 4. 사용자 & 지갑
- **사용자 등록**: 계정 생성 시 지갑 자동 생성
- **잔액 충전**: 현금 충전 기능
- **잔액 조회**: 실시간 현금/포인트 잔액 확인

## 🛠 기술 스택

### Backend
- **Java 17**: OpenJDK 17
- **Spring Boot 3.5.7**: 최신 Spring Boot 프레임워크
- **Spring Data JPA**: 데이터 영속성 관리
- **QueryDSL 5.1.0**: 타입 안전한 동적 쿼리
- **Spring Data Redis**: 대기열 및 토큰 관리
- **Hibernate Validator**: Bean Validation 처리

### Database
- **H2 Database**: 개발 및 테스트용 인메모리 DB
- **Redis**: 대기열 및 활성 토큰 관리 (휘발성 데이터)

### Code Generation & Utilities
- **Lombok**: 보일러플레이트 코드 자동 생성
- **MapStruct 1.6.3**: DTO-Entity 매핑

### Documentation
- **Swagger (SpringDoc) 2.2.0**: API 문서 자동 생성

### Testing
- **JUnit 5**: 단위 테스트 프레임워크
- **Mockito**: 모킹 라이브러리
- **Spring Boot Test**: 통합 테스트 지원

### Build Tool
- **Gradle 8.14.3**: 빌드 자동화

## 🏗 프로젝트 구조

이 프로젝트는 **Clean Architecture**를 따릅니다.

```
src/main/java/com/example/queue/seat_reservation/
│
├── domain/                           # Domain Layer
│   ├── user/entity/User
│   ├── wallet/entity/Wallet
│   ├── seat/entity/Seat
│   ├── reservation/entity/Reservation
│   ├── payment/entity/Payment, PaymentHistory
│   └── token/entity/TokenHistory
│
├── application/                      # Application Layer
│   ├── {feature}/
│   │   ├── service/                 # 도메인별 핵심 비즈니스 로직 (재사용 가능)
│   │   ├── usecase/                 # Service를 조합한 완전한 비즈니스 기능
│   │   ├── adaptor/                 # Port 인터페이스 (의존성 역전)
│   │   └── dto/                     # 데이터 전송 객체
│   └── exception/
│       └── ErrorCode                 # 비즈니스 에러 코드 정의
│
├── infrastructure/                   # Infrastructure Layer
│   ├── {feature}/
│   │   ├── adaptor/                 # Adaptor 구현체 (JPA, Redis 등)
│   │   └── repository/              # Spring Data JPA Repository
│   ├── config/                       # 설정 (Redis, Swagger 등)
│   └── exception/                    # 예외 처리 핸들러
│
└── interfaces/                       # Presentation Layer
    ├── {feature}/
    │   ├── controller/              # REST API Controller
    │   └── swagger/                 # Swagger 문서 정의
    └── common/
        ├── response/ResponseDto     # 공통 응답 형식
        └── version/ApiVersion       # API 버전 관리
```

### 아키텍처 특징

#### 1. **Service vs UseCase 분리** (모범 사례)
```java
// Service: 재사용 가능한 단위 기능
public class UserService {
    public User getUser(String userId) { ... }
    public void registerUser(User user) { ... }
}

// UseCase: 완전한 비즈니스 요구사항 (여러 Service 조합)
public class QueueUseCase {
    public IssueQueueResponseDto issueQueueToken(IssueQueueRequestDto dto) {
        // 1. 사용자 검증 (UserService)
        // 2. 토큰 생성 (TokenService)
        // 3. 대기열 추가 (QueueService)
        // 4. 응답 생성
    }
}
```

#### 2. **의존성 역전 원칙 (DIP)**
```
Application (Port) ──implements→ Infrastructure (Adapter)
     ↓ depends on
   Domain
```

#### 3. **계층별 책임**
- **Domain**: 순수 비즈니스 엔티티 (프레임워크 독립)
- **Application**: 비즈니스 로직 및 Port 정의
- **Infrastructure**: 기술 구현 (JPA, Redis 등)
- **Interfaces**: 외부와의 통신 (REST API)

## 🚀 시작하기

### 요구사항

- **Java**: 17 이상
- **Gradle**: 8.x
- **Redis**: (선택) 대기열 기능 사용 시 필요

### 설치 및 실행

#### 1. 프로젝트 클론
```bash
git clone <repository-url>
cd seat-reservation
```

#### 2. 빌드
```bash
# 프로젝트 빌드
./gradlew build

# 테스트 제외하고 빌드
./gradlew build -x test

# 클린 빌드
./gradlew clean build
```

#### 3. 실행
```bash
# Spring Boot 애플리케이션 실행
./gradlew bootRun

# 또는 JAR 파일 직접 실행
java -jar build/libs/seat-reservation-0.0.1.jar
```

#### 4. 접속
- **애플리케이션**: http://localhost:59423
- **H2 Console**: http://localhost:59423/h2-console
  - JDBC URL: `jdbc:h2:mem:concertdb`
  - Username: `sa`
  - Password: (비어있음)
- **Swagger UI**: http://localhost:59423/swagger-ui.html

### 빌드 명령어

```bash
# 단위 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests UserServiceTest

# 특정 테스트 메소드 실행
./gradlew test --tests WalletServiceTest.get_wallet

# 의존성 확인
./gradlew dependencies

# 프로젝트 정보 확인
./gradlew projects
```

## 📚 API 문서

### API 버전
현재 API 버전: **v1** (`/v1/*`)

### 주요 엔드포인트

#### 대기열 API
| Method | Endpoint | 설명 | 상태 |
|--------|----------|------|------|
| POST | `/v1/queue/token` | 대기열 토큰 발급 | ✅ 완료 |
| GET | `/v1/queue/status` | 대기 순서 조회 | ✅ 완료 |

#### 좌석 API
| Method | Endpoint | 설명 | 상태 |
|--------|----------|------|------|
| GET | `/v1/seats` | 예약 가능한 좌석 목록 조회 | ✅ 완료 |
| POST | `/v1/seats/temp-reserve` | 좌석 임시 예약 | ⚠️ 진행 중 |

#### 결제 API
| Method | Endpoint | 설명 | 상태 |
|--------|----------|------|------|
| POST | `/v1/payment` | 결제 처리 | ✅ 완료 |

#### 사용자 & 지갑 API
| Method | Endpoint | 설명 | 상태 |
|--------|----------|------|------|
| POST | `/v1/users` | 사용자 등록 | ✅ 완료 |
| POST | `/v1/wallet/charge` | 잔액 충전 | ✅ 완료 |
| GET | `/v1/wallet/{userId}` | 잔액 조회 | ✅ 완료 |

### API 응답 형식

모든 API는 다음 공통 형식을 따릅니다:

```json
{
  "errorCode": "SU000",
  "msg": "ok",
  "data": { ... }
}
```

자세한 API 명세는 [API Specification 문서](docs/api-specification.md) 또는 Swagger UI를 참조하세요.

## 💾 데이터베이스

### ERD (Entity Relationship Diagram)

```
User (1:1) Wallet
 │              │
 │ (1:N)        │ (1:N)
 ↓              ↓
Reservation  PaymentHistory
 │
 │ (N:1)
 ↓
Seat

Payment (1:1) Reservation

TokenHistory (독립적)
```

### 주요 엔티티

#### User (사용자)
- `user_id` (PK): 사용자 고유 ID
- `name`: 이름
- `email`: 이메일 (UNIQUE)
- `created_at`: 생성 시간

#### Wallet (지갑)
- `wallet_id` (PK): 지갑 고유 ID
- `user_id` (FK): 사용자 ID (1:1)
- `cash`: 현금 잔액
- `point`: 포인트 잔액
- `updated_at`: 마지막 업데이트 시간

#### Seat (좌석)
- `seat_id` (PK): 좌석 ID
- `seat_number`: 좌석 번호
- `price`: 가격
- `status`: 상태 (AVAILABLE, TEMP_RESERVED, CONFIRMED)

#### Reservation (예약)
- `reservation_id` (PK): 예약 ID
- `user_id` (FK): 사용자 ID
- `seat_id` (FK): 좌석 ID
- `status`: 예약 상태
- `price`: 예약 시점 가격
- `reserved_at`: 예약 시간
- `expires_at`: 만료 시간
- `confirmed_at`: 확정 시간

#### Payment (결제)
- `payment_id` (PK): 결제 ID
- `reservation_id` (FK): 예약 ID (1:1)
- `user_id` (FK): 사용자 ID
- `total_amount`: 총 금액
- `point_used`: 사용 포인트
- `cash_used`: 사용 현금
- `point_earned`: 적립 포인트
- `paid_at`: 결제 시간

#### PaymentHistory (결제 내역)
- `history_id` (PK): 내역 ID
- `wallet_id` (FK): 지갑 ID
- `type`: 거래 유형 (CHARGE, PAYMENT_CASH, PAYMENT_POINT, POINT_EARN)
- `amount`: 변동 금액
- `cash_after`: 거래 후 현금 잔액
- `point_after`: 거래 후 포인트 잔액
- `description`: 거래 설명
- `created_at`: 거래 시간

#### TokenHistory (토큰 히스토리)
- `token` (PK): 토큰 값 (UUID)
- `user_id` (FK): 사용자 ID
- `status`: 토큰 상태 (WAITING, ACTIVE, EXPIRED)
- `created_at`: 생성 시간
- `activated_at`: 활성화 시간
- `expired_at`: 만료 시간

### Redis 데이터 구조

#### 대기열 토큰
- **Key**: `queue:token:{token}`
- **Type**: Hash
- **TTL**: ACTIVE 상태일 때 1800초 (30분)

#### 대기열
- **Key**: `queue:waiting`
- **Type**: Sorted Set (ZSET)
- **Score**: 생성 시간 (timestamp)

#### 활성 토큰 Set
- **Key**: `queue:active`
- **Type**: Set
- **용도**: ACTIVE 상태 토큰 관리

자세한 데이터 모델은 [Data Model 문서](docs/data-model.md)를 참조하세요.

## 🧪 테스트

### 테스트 구조

```
src/test/java/
├── application/
│   ├── user/service/UserServiceTest
│   ├── wallet/service/WalletServiceTest
│   ├── token/service/TokenServiceTest
│   ├── queue/service/QueueServiceTest
│   ├── seat/service/SeatServiceConcurrencyTest  # 동시성 테스트
│   └── ...
└── domain/
    ├── user/entity/UserTest
    ├── wallet/entity/WalletTest
    └── ...
```

### 테스트 실행

```bash
# 전체 테스트 실행
./gradlew test

# 특정 패키지 테스트
./gradlew test --tests com.example.queue.seat_reservation.application.*

# 동시성 테스트만 실행
./gradlew test --tests "*ConcurrencyTest"

# 테스트 리포트 생성
./gradlew test --info
```

### 테스트 커버리지

- **단위 테스트**: Service 계층 로직 검증
- **통합 테스트**: Repository 및 트랜잭션 검증
- **동시성 테스트**: 좌석 예약 동시 요청 처리 검증

## 📖 문서

프로젝트의 상세한 문서는 `docs/` 디렉토리에서 확인할 수 있습니다:

- **[requirements.md](docs/requirements.md)**: 시스템 요구사항 및 기능 명세
- **[user-stories.md](docs/user-stories.md)**: 사용자 스토리 및 Acceptance Criteria
- **[data-model.md](docs/data-model.md)**: 데이터베이스 설계 및 ERD
- **[api-specification.md](docs/api-specification.md)**: RESTful API 상세 명세
- **[CLAUDE.md](CLAUDE.md)**: 개발 가이드 및 아키텍처 설명

## 🔑 핵심 설계 결정

### 1. Clean Architecture 채택
- **이유**: 비즈니스 로직과 기술 구현의 완전한 분리
- **효과**: 테스트 용이성, 기술 스택 변경 유연성, 유지보수성 향상

### 2. Service/UseCase 분리
- **이유**: 재사용 가능한 빌딩 블록(Service) + 완전한 비즈니스 흐름(UseCase)
- **효과**: 코드 재사용성, 테스트 전략 명확화, 비즈니스 로직 가독성 향상

### 3. 비관적 락 사용
- **이유**: 좌석 예약의 이중 예약 절대 방지 필요
- **효과**: 데이터 무결성 보장, 동시성 문제 해결

### 4. PaymentHistory 설계 개선
- **변경**: `balance_after` → `cash_after`, `point_after`
- **이유**: 현금과 포인트를 별도로 추적하여 더 명확한 이력 관리
- **효과**: 감사 및 디버깅 용이성 향상

## 📝 라이선스

이 프로젝트는 학습 목적으로 제작되었습니다.
---

**Built with** ❤️ **using Spring Boot 3 & Clean Architecture**
