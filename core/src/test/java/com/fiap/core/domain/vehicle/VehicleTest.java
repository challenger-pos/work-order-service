package com.fiap.core.domain.vehicle;

import com.fiap.core.domain.customer.Customer;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class VehicleTest {

    @Test
    void shouldCreateVehicleWithAllFields() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        Customer customer = new Customer();
        customer.setId(UUID.randomUUID());

        String licensePlate = "ABC-1234";
        String brand = "Toyota";
        String model = "Corolla";
        Integer year = 2022;
        OffsetDateTime now = OffsetDateTime.now();

        // Act
        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .customer(customer)
                .licensePlate(licensePlate)
                .brand(brand)
                .model(model)
                .year(year)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(vehicleId, vehicle.getId());
        assertEquals(customer, vehicle.getCustomer());
        assertEquals(licensePlate, vehicle.getLicensePlate());
        assertEquals(brand, vehicle.getBrand());
        assertEquals(model, vehicle.getModel());
        assertEquals(year, vehicle.getYear());
    }

    @Test
    void shouldCreateVehicleWithIdOnly() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();

        // Act
        Vehicle vehicle = new Vehicle(vehicleId);

        // Assert
        assertEquals(vehicleId, vehicle.getId());
        assertNull(vehicle.getCustomer());
        assertNull(vehicle.getLicensePlate());
    }

    @Test
    void shouldUpdateVehicleFields() {
        // Arrange
        Vehicle vehicle = new Vehicle();
        vehicle.setId(UUID.randomUUID());
        vehicle.setLicensePlate("XYZ-9876");
        vehicle.setBrand("Honda");
        vehicle.setModel("Civic");
        vehicle.setYear(2023);

        // Act
        vehicle.setLicensePlate("DEF-5678");
        vehicle.setBrand("Volkswagen");

        // Assert
        assertEquals("DEF-5678", vehicle.getLicensePlate());
        assertEquals("Volkswagen", vehicle.getBrand());
        assertEquals("Civic", vehicle.getModel());
    }

    @Test
    void shouldCompareTwoVehicles() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();
        Vehicle vehicle1 = Vehicle.builder()
                .id(vehicleId)
                .licensePlate("ABC-1234")
                .brand("Toyota")
                .build();

        Vehicle vehicle2 = Vehicle.builder()
                .id(vehicleId)
                .licensePlate("ABC-1234")
                .brand("Toyota")
                .build();

        // Act & Assert
        assertEquals(vehicle1, vehicle2);
    }

    @Test
    void shouldHandleNullCustomer() {
        // Arrange
        UUID vehicleId = UUID.randomUUID();

        // Act
        Vehicle vehicle = Vehicle.builder()
                .id(vehicleId)
                .licensePlate("ABC-1234")
                .customer(null)
                .build();

        // Assert
        assertNull(vehicle.getCustomer());
    }
}
