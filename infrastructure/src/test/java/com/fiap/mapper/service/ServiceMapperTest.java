package com.fiap.mapper.service;

import com.fiap.core.domain.service.Service;
import com.fiap.dto.service.CreateServiceRequest;
import com.fiap.dto.service.ServiceResponse;
import com.fiap.dto.service.UpdateServiceRequest;
import com.fiap.persistence.entity.service.ServiceEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServiceMapperTest {

    @InjectMocks
    private ServiceMapper serviceMapper;

    @Test
    void shouldMapCreateServiceRequestToDomain() {
        // Arrange
        CreateServiceRequest request = new CreateServiceRequest(
                "Alinhamento",
                "Alinhamento 3D de pneus",
                new BigDecimal("80.00"),
                45
        );

        // Act
        Service result = serviceMapper.toDomain(request);

        // Assert
        assertNotNull(result);
        assertEquals("Alinhamento", result.getName());
        assertEquals("Alinhamento 3D de pneus", result.getDescription());
        assertEquals(new BigDecimal("80.00"), result.getBasePrice());
        assertEquals(45, result.getEstimatedTimeMin());
    }

    @Test
    void shouldMapUpdateServiceRequestToDomain() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        UpdateServiceRequest request = new UpdateServiceRequest(
                "Balanceamento",
                "Balanceamento de rodas",
                new BigDecimal("120.00"),
                30
        );

        // Act
        Service result = serviceMapper.toDomain(serviceId, request);

        // Assert
        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        assertEquals("Balanceamento", result.getName());
        assertEquals(new BigDecimal("120.00"), result.getBasePrice());
    }

    @Test
    void shouldMapServiceToResponse() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        Service service = Service.builder()
                .id(serviceId)
                .name("Troca de Óleo")
                .description("Troca de óleo e filtro")
                .basePrice(new BigDecimal("150.00"))
                .estimatedTimeMin(60)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Act
        ServiceResponse result = serviceMapper.toResponse(service);

        // Assert
        assertNotNull(result);
        assertEquals(serviceId, result.id());
        assertEquals("Troca de Óleo", result.name());
        assertEquals("Troca de óleo e filtro", result.description());
        assertEquals(new BigDecimal("150.00"), result.basePrice());
    }

    @Test
    void shouldMapServiceToEntity() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Service service = Service.builder()
                .id(serviceId)
                .name("Revisão")
                .description("Revisão completa")
                .basePrice(new BigDecimal("300.00"))
                .estimatedTimeMin(120)
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        ServiceEntity result = serviceMapper.toEntity(service);

        // Assert
        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        assertEquals("Revisão", result.getName());
        assertEquals(new BigDecimal("300.00"), result.getBasePrice());
    }

    @Test
    void shouldMapServiceEntityToDomain() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        ServiceEntity entity = ServiceEntity.builder()
                .id(serviceId)
                .name("Freios")
                .description("Substituição de pastilhas")
                .basePrice(new BigDecimal("250.00"))
                .estimatedTimeMin(90)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Act
        Service result = serviceMapper.toDomain(entity);

        // Assert
        assertNotNull(result);
        assertEquals(serviceId, result.getId());
        assertEquals("Freios", result.getName());
        assertEquals("Substituição de pastilhas", result.getDescription());
    }
}
