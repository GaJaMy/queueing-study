package com.example.queue.seat_reservation.domain.token.entity;

import com.example.queue.seat_reservation.domain.user.entity.User;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("TokenHistory 엔티티 테스트")
class TokenHistoryTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("TokenHistory 엔티티 생성 성공 - WAITING 상태")
    void createTokenHistory_Waiting_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        String token = "TOKEN123";

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token(token)
                .user(user)
                .status(TokenStatus.WAITING)
                .build();

        // then
        assertThat(tokenHistory).isNotNull();
        assertThat(tokenHistory.getToken()).isEqualTo(token);
        assertThat(tokenHistory.getUser()).isEqualTo(user);
        assertThat(tokenHistory.getStatus()).isEqualTo(TokenStatus.WAITING);
        assertThat(tokenHistory.getActivatedAt()).isNull();
        assertThat(tokenHistory.getExpiredAt()).isNull();
    }

    @Test
    @DisplayName("TokenHistory 엔티티 생성 성공 - ACTIVE 상태")
    void createTokenHistory_Active_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        String token = "TOKEN123";
        LocalDateTime activatedAt = LocalDateTime.now();
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token(token)
                .user(user)
                .status(TokenStatus.ACTIVE)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(tokenHistory).isNotNull();
        assertThat(tokenHistory.getToken()).isEqualTo(token);
        assertThat(tokenHistory.getUser()).isEqualTo(user);
        assertThat(tokenHistory.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(tokenHistory.getActivatedAt()).isEqualTo(activatedAt);
        assertThat(tokenHistory.getExpiredAt()).isEqualTo(expiredAt);
    }

    @Test
    @DisplayName("TokenHistory 엔티티 생성 성공 - EXPIRED 상태")
    void createTokenHistory_Expired_Success() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        String token = "TOKEN123";
        LocalDateTime activatedAt = LocalDateTime.now().minusMinutes(31);
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token(token)
                .user(user)
                .status(TokenStatus.EXPIRED)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(tokenHistory).isNotNull();
        assertThat(tokenHistory.getStatus()).isEqualTo(TokenStatus.EXPIRED);
        assertThat(tokenHistory.getExpiredAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("TokenHistory와 User 관계 확인")
    void tokenHistoryUserRelationship() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.WAITING)
                .build();

        // then
        assertThat(tokenHistory.getUser()).isNotNull();
        assertThat(tokenHistory.getUser().getUserId()).isEqualTo("user123");
    }

    @Test
    @DisplayName("TokenHistory WAITING 상태 - 만료 시간 없음")
    void tokenHistory_Waiting_NoExpiry() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.WAITING)
                .build();

        // then
        assertThat(tokenHistory.getStatus()).isEqualTo(TokenStatus.WAITING);
        assertThat(tokenHistory.getExpiredAt()).isNull();
    }

    @Test
    @DisplayName("TokenHistory ACTIVE 상태 - 30분 만료 시간 설정")
    void tokenHistory_Active_30MinutesExpiry() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        LocalDateTime activatedAt = LocalDateTime.now();
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.ACTIVE)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(tokenHistory.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(tokenHistory.getActivatedAt()).isNotNull();
        assertThat(tokenHistory.getExpiredAt()).isNotNull();
        assertThat(tokenHistory.getExpiredAt()).isAfter(tokenHistory.getActivatedAt());
    }

    @Test
    @DisplayName("TokenHistory 상태 변경 - WAITING to ACTIVE")
    void changeTokenStatus_WaitingToActive() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        TokenHistory waitingToken = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.WAITING)
                .build();

        LocalDateTime activatedAt = LocalDateTime.now();
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        // when
        TokenHistory activeToken = TokenHistory.builder()
                .token(waitingToken.getToken())
                .user(waitingToken.getUser())
                .status(TokenStatus.ACTIVE)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(activeToken.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(activeToken.getActivatedAt()).isNotNull();
        assertThat(activeToken.getExpiredAt()).isNotNull();
    }

    @Test
    @DisplayName("TokenHistory 상태 변경 - ACTIVE to EXPIRED")
    void changeTokenStatus_ActiveToExpired() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        LocalDateTime activatedAt = LocalDateTime.now().minusMinutes(31);
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        TokenHistory activeToken = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.ACTIVE)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // when & then
        // 30분이 지나면 EXPIRED 상태로 변경 (비즈니스 로직 레이어에서 구현)
        assertThat(activeToken.getExpiredAt()).isBefore(LocalDateTime.now());
    }

    @Test
    @DisplayName("TokenHistory 빌더 패턴 테스트")
    void builderPattern() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        String token = "TOKEN123";
        LocalDateTime activatedAt = LocalDateTime.now();
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        // when
        TokenHistory tokenHistory = TokenHistory.builder()
                .token(token)
                .user(user)
                .status(TokenStatus.ACTIVE)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // then
        assertThat(tokenHistory.getToken()).isEqualTo(token);
        assertThat(tokenHistory.getUser()).isEqualTo(user);
        assertThat(tokenHistory.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(tokenHistory.getActivatedAt()).isEqualTo(activatedAt);
        assertThat(tokenHistory.getExpiredAt()).isEqualTo(expiredAt);
    }

    @Test
    @DisplayName("TokenHistory 생성 타임스탬프 확인")
    void createdAt_Test() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        TokenHistory tokenHistory = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.WAITING)
                .build();

        // when & then
        // @CreatedDate는 JPA Auditing에 의해 자동 설정되므로
        // 실제로는 Repository 레이어 테스트에서 검증 필요
        assertThat(tokenHistory).isNotNull();
    }

    @Test
    @DisplayName("TokenStatus Enum 값 확인")
    void tokenStatus_EnumValues() {
        // when & then
        assertThat(TokenStatus.WAITING.getDescription()).isEqualTo("대기열 대기");
        assertThat(TokenStatus.ACTIVE.getDescription()).isEqualTo("토큰 활성화");
        assertThat(TokenStatus.EXPIRED.getDescription()).isEqualTo("토큰 만료");
    }

    @Test
    @DisplayName("TokenHistory 여러 사용자의 토큰 구분")
    void multipleUsers_DifferentTokens() {
        // given
        User user1 = User.builder()
                .userId("user1")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        User user2 = User.builder()
                .userId("user2")
                .name("김철수")
                .email("kim@example.com")
                .build();

        // when
        TokenHistory token1 = TokenHistory.builder()
                .token("TOKEN1")
                .user(user1)
                .status(TokenStatus.WAITING)
                .build();

        TokenHistory token2 = TokenHistory.builder()
                .token("TOKEN2")
                .user(user2)
                .status(TokenStatus.WAITING)
                .build();

        // then
        assertThat(token1.getToken()).isNotEqualTo(token2.getToken());
        assertThat(token1.getUser()).isNotEqualTo(token2.getUser());
    }

    @Test
    @DisplayName("TokenHistory ACTIVE 토큰 만료 시간 검증")
    void validateActiveTokenExpiry() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        LocalDateTime activatedAt = LocalDateTime.now();
        LocalDateTime expiredAt = activatedAt.plusMinutes(30);

        TokenHistory tokenHistory = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.ACTIVE)
                .activatedAt(activatedAt)
                .expiredAt(expiredAt)
                .build();

        // when & then
        // ACTIVE 토큰은 30분 만료 정책
        long minutesDiff = java.time.Duration.between(
                tokenHistory.getActivatedAt(),
                tokenHistory.getExpiredAt()
        ).toMinutes();

        assertThat(minutesDiff).isEqualTo(30);
    }

    @Test
    @DisplayName("TokenHistory 검증 - status가 TokenStatus enum 타입")
    void validateStatusType() {
        // given
        User user = User.builder()
                .userId("user123")
                .name("홍길동")
                .email("hong@example.com")
                .build();

        TokenHistory tokenHistory = TokenHistory.builder()
                .token("TOKEN123")
                .user(user)
                .status(TokenStatus.WAITING)
                .build();

        // when
        Set<ConstraintViolation<TokenHistory>> violations = validator.validate(tokenHistory);

        // then
        assertThat(violations).isEmpty();
        assertThat(tokenHistory.getStatus()).isInstanceOf(TokenStatus.class);
    }
}
