package com.fiap.gateway.customer;

import com.fiap.persistence.repository.customer.CustomerEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentNumberAvailableGatewayImplTest {

    @Mock
    private CustomerEntityRepository customerEntityRepository;

    @InjectMocks
    private DocumentNumberAvailableGatewayImpl documentNumberAvailableGateway;

    @Test
    void shouldReturnTrueWhenDocumentNumberIsAvailable() {
        // Arrange
        String documentNumber = "12345678900";
        when(customerEntityRepository.existsByDocumentNumber(documentNumber)).thenReturn(false);

        // Act
        Boolean result = documentNumberAvailableGateway.documentNumberAvailable(documentNumber);

        // Assert
        assertTrue(result);
        verify(customerEntityRepository).existsByDocumentNumber(documentNumber);
    }

    @Test
    void shouldReturnFalseWhenDocumentNumberIsNotAvailable() {
        // Arrange
        String documentNumber = "98765432100";
        when(customerEntityRepository.existsByDocumentNumber(documentNumber)).thenReturn(true);

        // Act
        Boolean result = documentNumberAvailableGateway.documentNumberAvailable(documentNumber);

        // Assert
        assertFalse(result);
        verify(customerEntityRepository).existsByDocumentNumber(documentNumber);
    }

    @Test
    void shouldCheckMultipleDifferentDocumentNumbers() {
        // Arrange
        String availableDocument = "11122233344";
        String takenDocument = "55566677788";

        when(customerEntityRepository.existsByDocumentNumber(availableDocument)).thenReturn(false);
        when(customerEntityRepository.existsByDocumentNumber(takenDocument)).thenReturn(true);

        // Act & Assert
        assertTrue(documentNumberAvailableGateway.documentNumberAvailable(availableDocument));
        assertFalse(documentNumberAvailableGateway.documentNumberAvailable(takenDocument));

        verify(customerEntityRepository, times(2)).existsByDocumentNumber(any());
    }
}
