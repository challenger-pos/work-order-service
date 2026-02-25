package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ForbiddenExceptionTest {

    @Test
    void shouldCreateForbiddenException() {
        // Arrange
        String message = "Forbidden access";
        String code = "FORBIDDEN";

        // Act
        ForbiddenException exception = new ForbiddenException(message, code);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Assert
        assertTrue(new ForbiddenException("Test", "CODE") instanceof DomainException);
    }
}
