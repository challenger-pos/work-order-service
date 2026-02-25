package com.fiap.gateway.customer;

import com.fiap.core.domain.customer.Customer;
import com.fiap.mapper.customer.CustomerMapper;
import com.fiap.persistence.entity.customer.CustomerEntity;
import com.fiap.persistence.repository.customer.CustomerEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerRepositoryGatewayTest {

    @Mock
    private CustomerEntityRepository customerEntityRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerRepositoryGateway customerRepositoryGateway;

    @Test
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customer.getId());

        when(customerMapper.toEntity(customer)).thenReturn(customerEntity);
        when(customerEntityRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        // Act
        Customer result = customerRepositoryGateway.create(customer);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        verify(customerMapper).toEntity(customer);
        verify(customerEntityRepository).save(customerEntity);
        verify(customerMapper).toDomain(customerEntity);
    }

    @Test
    void shouldUpdateCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customer.getId());

        when(customerMapper.toEntity(customer)).thenReturn(customerEntity);
        when(customerEntityRepository.save(customerEntity)).thenReturn(customerEntity);
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        // Act
        Customer result = customerRepositoryGateway.update(customer);

        // Assert
        assertNotNull(result);
        assertEquals(customer.getId(), result.getId());
        verify(customerEntityRepository).save(customerEntity);
    }

    @Test
    void shouldFindCustomerByIdSuccessfully() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(customerId);

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customerId);

        when(customerEntityRepository.findById(customerId)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        // Act
        Optional<Customer> result = customerRepositoryGateway.findById(customerId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(customer.getId(), result.get().getId());
        verify(customerEntityRepository).findById(customerId);
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        when(customerEntityRepository.findById(customerId)).thenReturn(Optional.empty());

        // Act
        Optional<Customer> result = customerRepositoryGateway.findById(customerId);

        // Assert
        assertFalse(result.isPresent());
        verify(customerEntityRepository).findById(customerId);
    }

    @Test
    void shouldFindCustomerByDocumentNumberSuccessfully() {
        // Arrange
        String documentNumber = "12345678900";
        Customer customer = new Customer();

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setDocumentNumber(documentNumber);

        when(customerEntityRepository.findByDocumentNumber(documentNumber)).thenReturn(Optional.of(customerEntity));
        when(customerMapper.toDomain(customerEntity)).thenReturn(customer);

        // Act
        Optional<Customer> result = customerRepositoryGateway.findByDocumentNumber(documentNumber);

        // Assert
        assertTrue(result.isPresent());
        verify(customerEntityRepository).findByDocumentNumber(documentNumber);
    }

    @Test
    void shouldDeleteCustomerSuccessfully() {
        // Arrange
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        CustomerEntity customerEntity = new CustomerEntity();
        customerEntity.setId(customer.getId());

        when(customerMapper.toEntity(customer)).thenReturn(customerEntity);
        doNothing().when(customerEntityRepository).delete(customerEntity);

        // Act
        customerRepositoryGateway.delete(customer);

        // Assert
        verify(customerEntityRepository).delete(customerEntity);
    }
}
