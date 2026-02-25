package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailExceptionTest {

    @Test
    void shouldCreateEmailException() {
        // Arrange
        String message = "Email already exists";
        String code = "EMAIL_EXISTS";

        // Act
        EmailException exception = new EmailException(message, code);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Assert
        assertTrue(new EmailException("Test", "CODE") instanceof DomainException);
    }
}
