package com.fiap.core.domain.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void shouldCreateLoginRequestWithEmailAndPassword() {
        // Arrange
        String email = "user@example.com";
        String password = "SecurePassword123!";

        // Act
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Assert
        assertNotNull(loginRequest);
        assertEquals(email, loginRequest.email());
        assertEquals(password, loginRequest.password());
    }

    @Test
    void shouldCreateLoginRequestWithDifferentCredentials() {
        // Arrange
        String email1 = "user1@example.com";
        String password1 = "Password@123";
        String email2 = "user2@example.com";
        String password2 = "Password@456";

        // Act
        LoginRequest request1 = new LoginRequest(email1, password1);
        LoginRequest request2 = new LoginRequest(email2, password2);

        // Assert
        assertEquals(email1, request1.email());
        assertEquals(password1, request1.password());
        assertEquals(email2, request2.email());
        assertEquals(password2, request2.password());
        assertNotEquals(request1, request2);
    }

    @Test
    void shouldHandleNullEmailAndPassword() {
        // Act
        LoginRequest loginRequest = new LoginRequest(null, null);

        // Assert
        assertNull(loginRequest.email());
        assertNull(loginRequest.password());
    }

    @Test
    void shouldHandleEmptyEmailAndPassword() {
        // Act
        LoginRequest loginRequest = new LoginRequest("", "");

        // Assert
        assertEquals("", loginRequest.email());
        assertEquals("", loginRequest.password());
    }

    @Test
    void shouldHandleComplexEmail() {
        // Arrange
        String complexEmail = "user+tag@example.co.uk";
        String password = "Pass@123";

        // Act
        LoginRequest loginRequest = new LoginRequest(complexEmail, password);

        // Assert
        assertEquals(complexEmail, loginRequest.email());
        assertEquals(password, loginRequest.password());
    }
}
