package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotFoundExceptionTest {

    @Test
    void shouldCreateNotFoundException() {
        // Arrange
        String message = "Resource not found";
        String code = "NOT_FOUND";

        // Act
        NotFoundException exception = new NotFoundException(message, code);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Arrange
        NotFoundException exception = new NotFoundException("Test", "TEST");

        // Assert
        assertTrue(exception instanceof DomainException);
    }

    @Test
    void shouldPreserveNotFoundCode() {
        // Arrange
        NotFoundException exception = new NotFoundException(
                "Customer not found",
                "CUSTOMER_NOT_FOUND"
        );

        // Assert
        assertEquals("CUSTOMER_NOT_FOUND", exception.getCode());
    }
}
