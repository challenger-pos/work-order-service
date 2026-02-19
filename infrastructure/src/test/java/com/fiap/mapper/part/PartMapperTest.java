package com.fiap.mapper.part;

import com.fiap.core.domain.part.Money;
import com.fiap.core.domain.part.Part;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.dto.part.CreatePartRequest;
import com.fiap.dto.part.PartResponse;
import com.fiap.dto.part.UpdatePartRequest;
import com.fiap.persistence.entity.part.PartEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PartMapperTest {

    @InjectMocks
    private PartMapper partMapper;

    @Test
    void shouldMapCreatePartRequestToDomain() throws BusinessRuleException {
        // Arrange
        CreatePartRequest request = new CreatePartRequest(
                "Filtro de Óleo",
                "Filtro original para óleo do motor",
                new BigDecimal("50.00")
        );

        // Act
        Part result = partMapper.toDomain(request);

        // Assert
        assertNotNull(result);
        assertEquals("Filtro de Óleo", result.getName());
        assertEquals("Filtro original para óleo do motor", result.getDescription());
        assertEquals(new BigDecimal("50.00"), result.getPrice().getValue());
    }

    @Test
    void shouldMapUpdatePartRequestToDomain() throws BusinessRuleException {
        // Arrange
        UUID partId = UUID.randomUUID();
        UpdatePartRequest request = new UpdatePartRequest(
                "Filtro de Ar",
                "Filtro de ar do motor",
                new BigDecimal("75.00")
        );

        // Act
        Part result = partMapper.toDomain(partId, request);

        // Assert
        assertNotNull(result);
        assertEquals(partId, result.getId());
        assertEquals("Filtro de Ar", result.getName());
        assertEquals(new BigDecimal("75.00"), result.getPrice().getValue());
    }

    @Test
    void shouldMapPartToResponse() throws BusinessRuleException {
        // Arrange
        UUID partId = UUID.randomUUID();
        Part part = Part.builder()
                .id(partId)
                .name("Bateria")
                .description("Bateria 12V")
                .price(Money.of(new BigDecimal("200.00")))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Act
        PartResponse result = partMapper.toResponse(part);

        // Assert
        assertNotNull(result);
        assertEquals(partId, result.id());
        assertEquals("Bateria", result.name());
        assertEquals("Bateria 12V", result.description());
        assertEquals(new BigDecimal("200.00"), result.price());
    }

    @Test
    void shouldMapPartToEntity() throws BusinessRuleException {
        // Arrange
        UUID partId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        Part part = Part.builder()
                .id(partId)
                .name("Vela de Ignição")
                .description("Vela padrão")
                .price(Money.of(new BigDecimal("30.00")))
                .createdAt(now)
                .updatedAt(now)
                .build();

        // Act
        PartEntity result = partMapper.toEntity(part);

        // Assert
        assertNotNull(result);
        assertEquals(partId, result.getId());
        assertEquals("Vela de Ignição", result.getName());
        assertEquals(new BigDecimal("30.00"), result.getPrice());
    }

    @Test
    void shouldMapPartEntityToDomain() {
        // Arrange
        UUID partId = UUID.randomUUID();
        PartEntity entity = PartEntity.builder()
                .id(partId)
                .name("Pneu")
                .description("Pneu 175/60R15")
                .price(new BigDecimal("250.00"))
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        // Act
        Part result = partMapper.toDomain(entity);

        // Assert
        assertNotNull(result);
        assertEquals(partId, result.getId());
        assertEquals("Pneu", result.getName());
    }
}
