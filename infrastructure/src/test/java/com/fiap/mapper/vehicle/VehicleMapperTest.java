package com.fiap.mapper.vehicle;

import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.dto.vehicle.CreateVehicleRequest;
import com.fiap.dto.vehicle.UpdateVehicleRequest;
import com.fiap.dto.vehicle.VehicleResponse;
import com.fiap.mapper.customer.CustomerMapper;
import com.fiap.persistence.entity.vehicle.VehicleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VehicleMapperTest {

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private VehicleMapper vehicleMapper;

    @Test
    void shouldMapCreateVehicleRequestToDomain() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        CreateVehicleRequest request = new CreateVehicleRequest(
                customerId,
                "ABC-1234",
                "Toyota",
                "Corolla",
                2022
        );

        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerMapper.toDomain(any(java.util.UUID.class))).thenReturn(customer);

        // Act
        Vehicle result = vehicleMapper.toDomain(request);

        // Assert
        assertNotNull(result);
        assertEquals("ABC-1234", result.getLicensePlate());
        assertEquals("Toyota", result.getBrand());
        assertEquals("Corolla", result.getModel());
        assertEquals(2022, result.getYear());
    }

    @Test
    void shouldMapUpdateVehicleRequestToDomain() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        UpdateVehicleRequest request = new UpdateVehicleRequest(
                customerId,
                "XYZ-5678",
                "Honda",
                "Civic",
                2023
        );

        Customer customer = new Customer();
        customer.setId(customerId);

        when(customerMapper.toDomain(any(java.util.UUID.class))).thenReturn(customer);

        // Act
        Vehicle result = vehicleMapper.toDomain(vehicleId, request);

        // Assert
        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        assertEquals("XYZ-5678", result.getLicensePlate());
        assertEquals("Honda", result.getBrand());
    }

    @Test
    void shouldMapVehicleToResponse() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);

        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .customer(customer)
                .licensePlate("DEF-9012")
                .brand("Volkswagen")
                .model("Gol")
                .year(2021)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        when(customerMapper.toResponse(any(Customer.class))).thenReturn(
            new com.fiap.dto.customer.CustomerResponse(customerId, "João", "joao@example.com", null)
        );

        // Act
        VehicleResponse result = vehicleMapper.toResponse(vehicle);

        // Assert
        assertNotNull(result);
        assertEquals(vehicleId, result.id());
        assertEquals("DEF-9012", result.licensePlate());
        assertEquals("Volkswagen", result.brand());
    }

    @Test
    void shouldMapVehicleToEntity() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Customer customer = new Customer();
        customer.setId(customerId);

        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .customer(customer)
                .licensePlate("GHI-3456")
                .brand("Fiat")
                .model("Uno")
                .year(2020)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Act
        VehicleEntity result = vehicleMapper.toEntity(vehicle);

        // Assert
        assertNotNull(result);
        assertEquals(vehicleId, result.getId());
        assertEquals("GHI-3456", result.getLicensePlate());
        assertEquals("Fiat", result.getBrand());
    }

}
