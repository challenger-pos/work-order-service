package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InternalServerErrorExceptionTest {

    @Test
    void shouldCreateInternalServerErrorException() {
        // Arrange
        String message = "Internal server error";
        String code = "INTERNAL_ERROR";

        // Act
        InternalServerErrorException exception = new InternalServerErrorException(message, code);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Assert
        assertTrue(new InternalServerErrorException("Test", "CODE") instanceof DomainException);
    }
}
