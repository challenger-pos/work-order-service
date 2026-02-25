package com.fiap.core.domain.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTest {

    @Test
    void shouldCreateServiceWithAllFields() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        String name = "Alinhamento";
        String description = "Alinhamento 3D de pneus";
        BigDecimal basePrice = new BigDecimal("80.00");
        Integer estimatedTimeMin = 45;
        OffsetDateTime now = OffsetDateTime.now();

        // Act
        Service service = Service.builder()
                .id(serviceId)
                .name(name)
                .description(description)
                .basePrice(basePrice)
                .estimatedTimeMin(estimatedTimeMin)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Assert
        assertEquals(serviceId, service.getId());
        assertEquals(name, service.getName());
        assertEquals(description, service.getDescription());
        assertEquals(basePrice, service.getBasePrice());
        assertEquals(estimatedTimeMin, service.getEstimatedTimeMin());
    }

    @Test
    void shouldCreateServiceWithMinimalFields() {
        // Arrange
        String name = "Balanceamento";

        // Act
        Service service = new Service();
        service.setName(name);

        // Assert
        assertEquals(name, service.getName());
        assertNull(service.getId());
        assertNull(service.getDescription());
    }

    @Test
    void shouldUpdateServiceFields() {
        // Arrange
        Service service = new Service();
        service.setId(UUID.randomUUID());
        service.setName("Troca de Óleo");
        service.setBasePrice(new BigDecimal("150.00"));

        // Act
        service.setName("Troca de Óleo e Filtro");
        service.setBasePrice(new BigDecimal("180.00"));

        // Assert
        assertEquals("Troca de Óleo e Filtro", service.getName());
        assertEquals(new BigDecimal("180.00"), service.getBasePrice());
    }

    @Test
    void shouldCompareTwoServices() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        Service service1 = Service.builder()
                .id(serviceId)
                .name("Revisão")
                .basePrice(new BigDecimal("300.00"))
                .build();

        Service service2 = Service.builder()
                .id(serviceId)
                .name("Revisão")
                .basePrice(new BigDecimal("300.00"))
                .build();

        // Act & Assert
        assertEquals(service1, service2);
    }

    @Test
    void shouldHandleNullDescription() {
        // Arrange
        String name = "Serviço sem descrição";

        // Act
        Service service = Service.builder()
                .name(name)
                .description(null)
                .build();

        // Assert
        assertEquals(name, service.getName());
        assertNull(service.getDescription());
    }

    @Test
    void shouldHandleDifferentPrices() {
        // Arrange
        Service service1 = Service.builder()
                .name("Service1")
                .basePrice(new BigDecimal("100.00"))
                .build();

        Service service2 = Service.builder()
                .name("Service1")
                .basePrice(new BigDecimal("200.00"))
                .build();

        // Act & Assert
        assertNotEquals(service1, service2);
    }
}
