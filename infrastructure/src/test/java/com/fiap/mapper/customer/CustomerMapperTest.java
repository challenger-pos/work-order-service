package com.fiap.mapper.customer;

import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.customer.DocumentNumber;
import com.fiap.core.exception.DocumentNumberException;
import com.fiap.core.exception.EmailException;
import com.fiap.dto.customer.CreateCustomerRequest;
import com.fiap.dto.customer.CustomerResponse;
import com.fiap.dto.customer.UpdateCustomerRequest;
import com.fiap.persistence.entity.customer.CustomerEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerMapperTest {

    @InjectMocks
    private CustomerMapper customerMapper;

    @Test
    void shouldMapCustomerToEntity() throws DocumentNumberException, EmailException {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
            customerId,
            "João Silva",
            DocumentNumber.fromPersistence("12345678900"),
            "11999999999",
            "joao@example.com"
        );

        // Act
        CustomerEntity entity = customerMapper.toEntity(customer);

        // Assert
        assertNotNull(entity);
        assertEquals(customerId, entity.getId());
        assertEquals("João Silva", entity.getName());
        assertEquals("12345678900", entity.getDocumentNumber());
        assertEquals("11999999999", entity.getPhone());
        assertEquals("joao@example.com", entity.getEmail());
    }

    @Test
    void shouldMapCustomerEntityToDomain() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        CustomerEntity entity = new CustomerEntity(
                customerId,
                "Maria Silva",
            "98765432100",
                "11988888888",
                "maria@example.com",
            null,
            null,
            LocalDateTime.now(),
            LocalDateTime.now(),
                true
        );

        // Act
        Customer customer = customerMapper.toDomain(entity);

        // Assert
        assertNotNull(customer);
        assertEquals(customerId, customer.getId());
        assertEquals("Maria Silva", customer.getName());
        assertEquals("11988888888", customer.getPhone());
        assertEquals(true, customer.getIsActive());
    }

    @Test
    void shouldMapCreateCustomerRequestToDomain() throws DocumentNumberException, EmailException {
        // Arrange
        CreateCustomerRequest request = new CreateCustomerRequest(
            "Carlos",
            "52998224725",
            "carlos@example.com",
            "11912345678"
        );

        // Act
        Customer customer = customerMapper.toDomain(request);

        // Assert
        assertNotNull(customer);
        assertEquals("Carlos", customer.getName());
        assertEquals("11912345678", customer.getPhone());
        assertEquals("carlos@example.com", customer.getEmail().getValue());
    }

    @Test
    void shouldMapUpdateCustomerRequestToDomain() throws DocumentNumberException, EmailException {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UpdateCustomerRequest request = new UpdateCustomerRequest(
            customerId,
            "Ana Silva",
            "52998224725",
            "ana@example.com",
            "11987654321"
        );

        // Act
        Customer customer = customerMapper.toDomain(customerId, request);

        // Assert
        assertNotNull(customer);
        assertEquals(customerId, customer.getId());
        assertEquals("Ana Silva", customer.getName());
        assertEquals("ana@example.com", customer.getEmail().getValue());
    }

    @Test
    void shouldMapCustomerToResponse() throws DocumentNumberException, EmailException {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer(
            customerId,
            "Pedro",
            DocumentNumber.fromPersistence("12345678900"),
            "11999999999",
            "pedro@example.com"
        );

        // Act
        CustomerResponse response = customerMapper.toResponse(customer);

        // Assert
        assertNotNull(response);
        assertEquals(customerId, response.id());
        assertEquals("Pedro", response.name());
        assertEquals("pedro@example.com", response.email());
    }

    @Test
    void shouldMapCustomerWithIdOnly() {
        // Arrange
        UUID customerId = UUID.randomUUID();

        // Act
        Customer customer = customerMapper.toDomain(customerId);

        // Assert
        assertNotNull(customer);
        assertEquals(customerId, customer.getId());
    }
}
