package com.fiap.gateway.part;

import com.fiap.persistence.repository.part.WorkOrderPartEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderPartRepositoryGatewayTest {

    @Mock
    private WorkOrderPartEntityRepository repository;

    @InjectMocks
    private WorkOrderPartRepositoryGateway gateway;

    @Test
    void shouldReturnTrueWhenPartIdExists() {
        // Arrange
        UUID partId = UUID.randomUUID();
        when(repository.existsByPartId(partId)).thenReturn(true);

        // Act
        boolean result = gateway.existsByPartId(partId);

        // Assert
        assertTrue(result);
    }

    @Test
    void shouldReturnFalseWhenPartIdDoesNotExist() {
        // Arrange
        UUID partId = UUID.randomUUID();
        when(repository.existsByPartId(partId)).thenReturn(false);

        // Act
        boolean result = gateway.existsByPartId(partId);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldHandleMultiplePartIdChecks() {
        // Arrange
        UUID partId1 = UUID.randomUUID();
        UUID partId2 = UUID.randomUUID();
        when(repository.existsByPartId(partId1)).thenReturn(true);
        when(repository.existsByPartId(partId2)).thenReturn(false);

        // Act
        boolean result1 = gateway.existsByPartId(partId1);
        boolean result2 = gateway.existsByPartId(partId2);

        // Assert
        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    void shouldCheckPartIdWithNull() {
        // Arrange
        when(repository.existsByPartId(null)).thenReturn(false);

        // Act
        boolean result = gateway.existsByPartId(null);

        // Assert
        assertFalse(result);
    }

    @Test
    void shouldHandleRepositoryException() {
        // Arrange
        UUID partId = UUID.randomUUID();
        when(repository.existsByPartId(partId)).thenThrow(new RuntimeException("Database error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> gateway.existsByPartId(partId));
    }
}
