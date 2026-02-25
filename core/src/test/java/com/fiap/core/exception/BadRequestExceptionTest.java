package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BadRequestExceptionTest {

    @Test
    void shouldCreateBadRequestException() {
        // Arrange
        String message = "Invalid input";
        String code = "INVALID_INPUT";

        // Act
        BadRequestException exception = new BadRequestException(message, code);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Arrange
        BadRequestException exception = new BadRequestException("Test message", "TEST_CODE");

        // Assert
        assertTrue(exception instanceof DomainException);
    }

    @Test
    void shouldPreserveMessageAndCode() {
        // Arrange
        String message = "Required field missing";
        String code = "MISSING_FIELD";

        // Act
        BadRequestException exception = new BadRequestException(message, code);

        // Assert
        assertEquals("Required field missing", exception.getMessage());
        assertEquals("MISSING_FIELD", exception.getCode());
    }

    @Test
    void shouldHandleNullMessage() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException(null, "ERROR_CODE");

        // Assert
        assertNull(exception.getMessage());
        assertEquals("ERROR_CODE", exception.getCode());
    }

    @Test
    void shouldHandleNullCode() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException("Error message", null);

        // Assert
        assertEquals("Error message", exception.getMessage());
        assertNull(exception.getCode());
    }

    @Test
    void shouldHandleEmptyMessageAndCode() {
        // Arrange & Act
        BadRequestException exception = new BadRequestException("", "");

        // Assert
        assertEquals("", exception.getMessage());
        assertEquals("", exception.getCode());
    }
}
