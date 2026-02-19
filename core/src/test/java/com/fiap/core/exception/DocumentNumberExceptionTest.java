package com.fiap.core.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentNumberExceptionTest {

    @Test
    void shouldCreateDocumentNumberException() {
        // Arrange
        String message = "Document already exists";
        String code = "DOCUMENT_ALREADY_EXISTS";

        // Act
        DocumentNumberException exception = new DocumentNumberException(message, code);

        // Assert
        assertNotNull(exception);
        assertEquals(message, exception.getMessage());
        assertEquals(code, exception.getCode());
    }

    @Test
    void shouldExtendDomainException() {
        // Arrange
        DocumentNumberException exception = new DocumentNumberException("Test", "TEST_CODE");

        // Assert
        assertTrue(exception instanceof DomainException);
    }

    @Test
    void shouldPreserveDocumentNumberCode() {
        // Arrange
        DocumentNumberException exception = new DocumentNumberException(
                "CPF/CNPJ already registered",
                "DOCUMENT_REGISTERED"
        );

        // Assert
        assertEquals("DOCUMENT_REGISTERED", exception.getCode());
    }
}
