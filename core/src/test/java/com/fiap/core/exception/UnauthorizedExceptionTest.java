package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnauthorizedExceptionTest {

    @Test
    void shouldCreateUnauthorizedException() {
        // Arrange
        String message = "Unauthorized access";
        String code = "UNAUTHORIZED";

        // Act
        UnauthorizedException exception = new UnauthorizedException(message, code);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Assert
        assertTrue(new UnauthorizedException("Test", "CODE") instanceof DomainException);
    }
}
