package com.fiap.core.domain.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenTest {

    @Test
    void shouldCreateTokenWithAccessToken() {
        // Arrange
        String accessToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        // Act
        Token token = new Token(accessToken);

        // Assert
        assertNotNull(token);
        assertEquals(accessToken, token.accessToken());
    }

    @Test
    void shouldCreateTokenWithDifferentAccessTokens() {
        // Arrange
        String token1 = "token_abc_123";
        String token2 = "token_xyz_789";

        // Act
        Token tokenRecord1 = new Token(token1);
        Token tokenRecord2 = new Token(token2);

        // Assert
        assertEquals(token1, tokenRecord1.accessToken());
        assertEquals(token2, tokenRecord2.accessToken());
        assertNotEquals(tokenRecord1.accessToken(), tokenRecord2.accessToken());
    }

    @Test
    void shouldHandleNullAccessToken() {
        // Act & Assert
        Token token = new Token(null);
        assertNull(token.accessToken());
    }

    @Test
    void shouldHandleEmptyAccessToken() {
        // Act & Assert
        Token token = new Token("");
        assertEquals("", token.accessToken());
    }
}
