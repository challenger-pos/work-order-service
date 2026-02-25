package com.fiap.gateway.vehicle;

import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.mapper.vehicle.VehicleMapper;
import com.fiap.persistence.entity.vehicle.VehicleEntity;
import com.fiap.persistence.repository.vehicle.VehicleEntityRepository;
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
class VehicleRepositoryGatewayTest {

    @Mock
    private VehicleEntityRepository repository;

    @Mock
    private VehicleMapper mapper;

    @InjectMocks
    private VehicleRepositoryGateway vehicleRepositoryGateway;

    @Test
    void shouldCreateVehicleSuccessfully() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setId(vehicleId);

        when(mapper.toEntity(vehicle)).thenReturn(vehicleEntity);
        when(repository.save(vehicleEntity)).thenReturn(vehicleEntity);
        when(mapper.toDomain(vehicleEntity)).thenReturn(vehicle);

        // Act
        Vehicle result = vehicleRepositoryGateway.create(vehicle);

        // Assert
        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        verify(mapper).toEntity(vehicle);
        verify(repository).save(vehicleEntity);
        verify(mapper).toDomain(vehicleEntity);
    }

    @Test
    void shouldUpdateVehicleSuccessfully() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);

        VehicleEntity vehicleEntity = new VehicleEntity();
        vehicleEntity.setId(vehicleId);

        when(mapper.toEntity(vehicle)).thenReturn(vehicleEntity);
        when(repository.save(vehicleEntity)).thenReturn(vehicleEntity);
        when(mapper.toDomain(vehicleEntity)).thenReturn(vehicle);

        // Act
        Vehicle result = vehicleRepositoryGateway.update(vehicle);

        // Assert
        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        verify(repository).save(vehicleEntity);
    }

    @Test
    void shouldDeleteVehicleSuccessfully() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        doNothing().when(repository).deleteById(vehicleId);

        // Act
        vehicleRepositoryGateway.delete(vehicleId);

        // Assert
        verify(repository).deleteById(vehicleId);
    }

    @Test
    void shouldCheckIfVehicleExistsById() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        when(repository.existsById(vehicleId)).thenReturn(true);

        // Act
        boolean result = vehicleRepositoryGateway.existsById(vehicleId);

        // Assert
        assertTrue(result);
        verify(repository).existsById(vehicleId);
    }

    @Test
    void shouldReturnFalseWhenVehicleDoesNotExist() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        when(repository.existsById(vehicleId)).thenReturn(false);

        // Act
        boolean result = vehicleRepositoryGateway.existsById(vehicleId);

        // Assert
        assertFalse(result);
        verify(repository).existsById(vehicleId);
    }
}
