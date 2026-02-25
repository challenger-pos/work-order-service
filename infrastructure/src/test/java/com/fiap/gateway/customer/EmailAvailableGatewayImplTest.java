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
class EmailAvailableGatewayImplTest {

    @Mock
    private CustomerEntityRepository customerEntityRepository;

    @InjectMocks
    private EmailAvailableGatewayImpl emailAvailableGateway;

    @Test
    void shouldReturnTrueWhenEmailIsAvailable() {
        // Arrange
        String email = "available@example.com";
        when(customerEntityRepository.existsByEmail(email)).thenReturn(false);

        // Act
        Boolean result = emailAvailableGateway.emailAvailable(email);

        // Assert
        assertTrue(result);
        verify(customerEntityRepository).existsByEmail(email);
    }

    @Test
    void shouldReturnFalseWhenEmailIsNotAvailable() {
        // Arrange
        String email = "taken@example.com";
        when(customerEntityRepository.existsByEmail(email)).thenReturn(true);

        // Act
        Boolean result = emailAvailableGateway.emailAvailable(email);

        // Assert
        assertFalse(result);
        verify(customerEntityRepository).existsByEmail(email);
    }

    @Test
    void shouldCheckMultipleDifferentEmails() {
        // Arrange
        String availableEmail = "new@example.com";
        String takenEmail = "existing@example.com";

        when(customerEntityRepository.existsByEmail(availableEmail)).thenReturn(false);
        when(customerEntityRepository.existsByEmail(takenEmail)).thenReturn(true);

        // Act & Assert
        assertTrue(emailAvailableGateway.emailAvailable(availableEmail));
        assertFalse(emailAvailableGateway.emailAvailable(takenEmail));

        verify(customerEntityRepository, times(2)).existsByEmail(any());
    }
}
