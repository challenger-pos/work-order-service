package com.fiap.gateway.service;

import com.fiap.core.domain.service.Service;
import com.fiap.mapper.service.ServiceMapper;
import com.fiap.persistence.entity.service.ServiceEntity;
import com.fiap.persistence.repository.service.ServiceEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceRepositoryGatewayTest {

    @Mock
    private ServiceEntityRepository repository;

    @Mock
    private ServiceMapper mapper;

    @InjectMocks
    private ServiceRepositoryGateway serviceRepositoryGateway;

    @Test
    void shouldCreateServiceSuccessfully() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        Service service = new Service();
        service.setId(serviceId);

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setId(serviceId);

        when(mapper.toEntity(service)).thenReturn(serviceEntity);
        when(repository.save(serviceEntity)).thenReturn(serviceEntity);
        when(mapper.toDomain(serviceEntity)).thenReturn(service);

        // Act
        Service result = serviceRepositoryGateway.create(service);

        // Assert
        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        verify(mapper).toEntity(service);
        verify(repository).save(serviceEntity);
    }

    @Test
    void shouldUpdateServiceSuccessfully() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        Service service = new Service();
        service.setId(serviceId);

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setId(serviceId);

        when(mapper.toEntity(service)).thenReturn(serviceEntity);
        when(repository.save(serviceEntity)).thenReturn(serviceEntity);
        when(mapper.toDomain(serviceEntity)).thenReturn(service);

        // Act
        Service result = serviceRepositoryGateway.update(service);

        // Assert
        assertNotNull(result);
        verify(repository).save(serviceEntity);
    }

    @Test
    void shouldDeleteServiceSuccessfully() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        doNothing().when(repository).deleteById(serviceId);

        // Act
        serviceRepositoryGateway.delete(serviceId);

        // Assert
        verify(repository).deleteById(serviceId);
    }

    @Test
    void shouldFindServiceByIdSuccessfully() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        Service service = new Service();
        service.setId(serviceId);

        ServiceEntity serviceEntity = new ServiceEntity();
        serviceEntity.setId(serviceId);

        when(repository.findById(serviceId)).thenReturn(Optional.of(serviceEntity));
        when(mapper.toDomain(serviceEntity)).thenReturn(service);

        // Act
        Optional<Service> result = serviceRepositoryGateway.findById(serviceId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(serviceId, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenServiceNotFound() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        when(repository.findById(serviceId)).thenReturn(Optional.empty());

        // Act
        Optional<Service> result = serviceRepositoryGateway.findById(serviceId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindMultipleServicesByIds() {
        // Arrange
        UUID serviceId1 = UUID.randomUUID();
        UUID serviceId2 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(serviceId1, serviceId2);

        Service service1 = new Service();
        service1.setId(serviceId1);

        Service service2 = new Service();
        service2.setId(serviceId2);

        ServiceEntity entity1 = new ServiceEntity();
        entity1.setId(serviceId1);

        ServiceEntity entity2 = new ServiceEntity();
        entity2.setId(serviceId2);

        when(repository.findAllById(ids)).thenReturn(Arrays.asList(entity1, entity2));
        when(mapper.toDomain(entity1)).thenReturn(service1);
        when(mapper.toDomain(entity2)).thenReturn(service2);

        // Act
        List<Service> results = serviceRepositoryGateway.findByIds(ids);

        // Assert
        assertEquals(2, results.size());
        verify(repository).findAllById(ids);
    }

    @Test
    void shouldCheckIfServiceExistsById() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        when(repository.existsById(serviceId)).thenReturn(true);

        // Act
        boolean result = serviceRepositoryGateway.existsById(serviceId);

        // Assert
        assertTrue(result);
    }
}
