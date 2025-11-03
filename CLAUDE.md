# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **seat reservation system with queue-based entry control**, built using Spring Boot 3.5.7 and Java 17. The system implements **Hexagonal (Ports & Adapters) Architecture** with clear separation between domain, application, infrastructure, and interface layers.

## Build Commands

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Run a single test class
./gradlew test --tests TokenServiceTest

# Run a specific test method
./gradlew test --tests WalletServiceTest.get_wallet

# Clean build
./gradlew clean build

# Run the application
./gradlew bootRun

# Check dependencies
./gradlew dependencies
```

## Development Setup

**H2 Console Access:**
- URL: http://localhost:59423/h2-console
- JDBC URL: `jdbc:h2:mem:concertdb`
- Username: `sa`
- Password: (empty)

**Swagger API Documentation:**
- Available at: http://localhost:59423/swagger-ui.html (when implemented)

**Application Port:** 59423

## Architectural Layers

The codebase follows strict hexagonal architecture with these layers:

### 1. Domain Layer (`domain/`)
Pure business entities with no external dependencies. Contains:
- **Entities:** User, Wallet, Seat, PaymentHistory, TokenHistory
- **Value Objects:** SeatStatus enum (AVAILABLE, RESERVED, TEMP_RESERVED)
- All entities use JPA auditing with `@CreatedDate` and `@LastModifiedDate`

### 2. Application Layer (`application/`)
Business logic and port interface definitions. Each feature module contains:
- **Service:** Orchestrates business operations
- **Adaptor (interface):** Port definition for data access

**Key Services:**
- `UserService` - User registration and retrieval
- `WalletService` - Wallet management
- `TokenService` - Queue token generation and management
- `TemporaryRepositoryService` - Coordinates temporary data storage (queue/tokens)
- `SeatService` - Seat reservation logic (under development)
- `PaymentService` - Payment processing (under development)

### 3. Infrastructure Layer (`infrastructure/`)
Technical implementations of ports. Contains:
- **JPA Adaptors:** Concrete implementations of port interfaces
- **Repositories:** Spring Data JPA repositories
- **RedisAdaptor:** Redis-based implementation for queue/token storage
- **Config:** RedisConfig, SwaggerConfig
- **Exception:** CustomException, ErrorCode enum

**Important:** All JPA adaptors inject `JPAQueryFactory` for complex queries using QueryDSL.

### 4. Interfaces Layer (`interfaces/`)
REST API controllers. Contains:
- `QueueController` - Queue management endpoints (`/v1/queue/*`)
- `SeatController` - Seat reservation endpoints (`/v1/seats/*`)
- `ResponseDto<T>` - Standardized API response wrapper

## Dependency Injection Pattern

All services and adaptors use **constructor-based dependency injection** via Lombok's `@RequiredArgsConstructor`:

```java
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserAdaptor userAdaptor;  // Injected via constructor
}
```

This ensures:
- Immutability (final fields)
- Easy mocking in tests
- Fail-fast on missing dependencies

## Testing Guidelines

Tests use JUnit 5 with Mockito and follow this structure:

```java
@ExtendWith(MockitoExtension.class)
class ServiceTest {
    @Mock
    private AdaptorInterface adaptor;

    @InjectMocks
    private ServiceClass service;

    @Test
    @DisplayName("한글 설명으로 테스트 목적 명시")
    void test_method() {
        // given - 테스트 데이터 준비
        // when - 테스트 실행
        // then - 검증 (assertNotNull, assertEquals, verify)
    }
}
```

**Key Testing Patterns:**
- Use `@DisplayName` with Korean descriptions
- Follow given-when-then structure
- Use Mockito's `when().thenReturn()` for mocking
- Verify interactions with `verify(mock, times(n))`
- Build test entities using builder pattern

## Exception Handling

All business exceptions use `CustomException` with `ErrorCode` enum:

```java
throw new CustomException(ErrorCode.NOT_EXIST_USER);
```

**Current Error Codes:**
- `NOT_EXIST_USER` (404) - "존재하지 않는 유저 입니다."
- `SUCCESS` (200) - "ok"
- `SERVER_ERROR` (200) - "서버 에러"

## Database Strategy

**Persistent Storage (H2/JPA):**
- User accounts, wallets, payment history
- Seat inventory and reservations
- Token history (audit trail)
- Schema auto-created via `ddl-auto: create` (change to `validate` in production)

**Temporary Storage (Redis):**
- Queue positions and active entries
- Session-based queue tokens
- Real-time queue status

**Note:** Redis configuration exists in `RedisConfig.java` but Redis adaptor methods are currently stubbed.

## Code Generation Tools

**Lombok:** Generates constructors, getters, builders
- `@RequiredArgsConstructor` for dependency injection
- `@Builder` for test data creation
- `@Getter` for entity accessors

**QueryDSL:** Type-safe query building
- Q-classes generated at compile time from entities
- Use `JPAQueryFactory` for complex queries

**MapStruct:** (Configured but not yet used)
- Will handle DTO-Entity mapping when implemented

## Adding New Features

When adding new functionality, follow the hexagonal architecture pattern:

1. **Define domain entity** in `domain/{feature}/entity/`
2. **Create port interface** in `application/{feature}/adaptor/`
3. **Implement service** in `application/{feature}/service/`
4. **Create concrete adaptor** in `infrastructure/{feature}/adaptor/`
5. **Create repository** in `infrastructure/{feature}/repository/`
6. **Add REST controller** in `interfaces/{feature}/controller/`
7. **Write tests** for service layer using Mockito

## Important Implementation Notes

- Services depend on **port interfaces** (adaptors), not concrete implementations
- All JPA repositories extend `JpaRepository<Entity, ID>`
- Use `Optional<>` return types for queries that may not find results
- Controller methods should return `ResponseDto<T>` wrapper
- API versioning uses `/v1` prefix (defined in `ApiVerSion.java`)
- All entities require audit fields (`createdAt`, `updatedAt`) with `@EntityListeners(AuditingEntityListener.class)`

## Entity Relationships

```
User (1) ←→ (1) Wallet
  ↓               ↓
  └─ reserves → Seat
  └─ temp reserves → Seat
                    ↓
               (M) PaymentHistory

TokenHistory (independent, for queue audit)
```

## Current Development Status

**Implemented:**
- Domain entities and relationships
- User and Wallet service logic
- JPA repository infrastructure
- Test suite structure (UserServiceTest, WalletServiceTest, TokenServiceTest)

**Under Development:**
- Token service implementation
- Redis adaptor implementation
- Queue management logic
- Seat reservation logic
- Payment processing
- REST endpoint implementations
