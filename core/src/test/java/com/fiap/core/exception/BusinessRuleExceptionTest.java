package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BusinessRuleExceptionTest {

    @Test
    void shouldCreateBusinessRuleException() {
        // Arrange
        String message = "Business rule violation";
        String code = "RULE_VIOLATION";

        // Act
        BusinessRuleException exception = new BusinessRuleException(message, code);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Assert
        assertTrue(new BusinessRuleException("Test", "CODE") instanceof DomainException);
    }
}
