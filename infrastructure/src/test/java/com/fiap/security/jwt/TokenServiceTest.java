package com.fiap.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.user.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private TokenService tokenService;
    private String secret = "my-super-secret-key-for-testing-purposes-only";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "secret", secret);
    }

    @Test
    void shouldGenerateValidToken() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.ADMIN);

        // Act
        String token = tokenService.generateToken(user);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertTrue(token.split("\\.").length == 3);
    }

    @Test
    void shouldValidateGeneratedToken() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.MECHANIC);
        String token = tokenService.generateToken(user);

        // Act
        String email = tokenService.validateToken(token);

        // Assert
        assertEquals("test@example.com", email);
    }

    @Test
    void shouldReturnEmptyStringForInvalidToken() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act
        String result = tokenService.validateToken(invalidToken);

        // Assert
        assertEquals("", result);
    }

    @Test
    void shouldReturnEmptyStringForExpiredToken() {
        // Arrange
        String expiredToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoLWFwaSIsInN1YiI6InRlc3RAZXhhbXBsZS5jb20iLCJyb2xlIjoiQURNSU4iLCJleHAiOjE2MDAwMDAwMDB9.invalid";

        // Act
        String result = tokenService.validateToken(expiredToken);

        // Assert
        assertEquals("", result);
    }

    @Test
    void shouldGenerateTokenWithCorrectIssuer() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setEmail("admin@example.com");
        user.setRole(UserRole.ADMIN);

        // Act
        String token = tokenService.generateToken(user);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String subject = JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getSubject();

        // Assert
        assertEquals("admin@example.com", subject);
    }

    @Test
    void shouldGenerateTokenWithExpirationTime() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.CLIENT);

        // Act
        String token = tokenService.generateToken(user);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant expiresAt = JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getExpiresAt()
                .toInstant();

        // Assert
        assertNotNull(expiresAt);
        assertTrue(expiresAt.isAfter(Instant.now()));
    }

    @Test
    void shouldGenerateTokenWithRoleClaim() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setEmail("mechanic@example.com");
        user.setRole(UserRole.MECHANIC);

        // Act
        String token = tokenService.generateToken(user);
        Algorithm algorithm = Algorithm.HMAC256(secret);
        String role = JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getClaim("role")
                .asString();

        // Assert
        assertEquals("MECHANIC", role);
    }

    @Test
    void shouldGenerateTokensWithDifferentEmails() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user1 = new User();
        user1.setEmail("user1@example.com");
        user1.setRole(UserRole.ADMIN);

        User user2 = new User();
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.CLIENT);

        // Act
        String token1 = tokenService.generateToken(user1);
        String token2 = tokenService.generateToken(user2);

        String email1 = tokenService.validateToken(token1);
        String email2 = tokenService.validateToken(token2);

        // Assert
        assertEquals("user1@example.com", email1);
        assertEquals("user2@example.com", email2);
        assertNotEquals(token1, token2);
    }

    @Test
    void shouldHandleMissingSecretGracefully() throws com.fiap.core.exception.EmailException {
        // Arrange
        ReflectionTestUtils.setField(tokenService, "secret", "");

        User user = new User();
        user.setEmail("test@example.com");
        user.setRole(UserRole.ADMIN);

        // Act & Assert
        assertThrows(Exception.class, () -> tokenService.generateToken(user));
    }
}
