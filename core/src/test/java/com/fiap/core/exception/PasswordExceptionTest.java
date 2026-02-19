package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordExceptionTest {

    @Test
    void shouldCreatePasswordException() {
        // Arrange
        String message = "Password too weak";
        String code = "WEAK_PASSWORD";

        // Act
        PasswordException exception = new PasswordException(message, code);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Assert
        assertTrue(new PasswordException("Test", "CODE") instanceof DomainException);
    }
}
