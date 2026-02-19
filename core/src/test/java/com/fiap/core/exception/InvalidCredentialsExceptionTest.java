package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidCredentialsExceptionTest {

    @Test
    void shouldCreateInvalidCredentialsException() {
        // Arrange
        String message = "Invalid email or password";

        // Act
        InvalidCredentialsException exception = new InvalidCredentialsException(message);

        // Assert
        assertEquals(message, exception.getMessage());
    }

    @Test
    void shouldExtendException() {
        // Assert
        assertTrue(new InvalidCredentialsException("Test") instanceof Exception);
    }

    @Test
    void shouldNotExtendDomainException() {
        // Assert
        Object exception = new InvalidCredentialsException("Test");
        assertFalse(exception instanceof DomainException);
    }

    @Test
    void shouldHandleNullMessage() {
        // Arrange & Act
        InvalidCredentialsException exception = new InvalidCredentialsException(null);

        // Assert
        assertNull(exception.getMessage());
    }

    @Test
    void shouldHandleEmptyMessage() {
        // Arrange & Act
        InvalidCredentialsException exception = new InvalidCredentialsException("");

        // Assert
        assertEquals("", exception.getMessage());
    }
}
