package com.fiap.mapper.workorder;

import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.dto.workorder.WorkOrderPartResponse;
import com.fiap.mapper.part.PartMapper;
import com.fiap.persistence.entity.workOrder.WorkOrderPartEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderPartMapperTest {

    @Mock
    private PartMapper partMapper;

    @InjectMocks
    private WorkOrderPartMapper workOrderPartMapper;

    @Test
    void shouldMapWorkOrderPartToResponse() {
        // Arrange
        UUID partId = UUID.randomUUID();
        Part part = new Part();
        part.setId(partId);
        part.setDescription("Part Test");

        WorkOrderPart workOrderPart = new WorkOrderPart(partId, 2);
        workOrderPart.setPart(part);
        workOrderPart.setQuantity(2);

        com.fiap.dto.part.PartResponse partResponse =
            new com.fiap.dto.part.PartResponse(partId, "Part Test", "desc", java.math.BigDecimal.ZERO, java.time.OffsetDateTime.now(), java.time.OffsetDateTime.now());

        // partMapper.toResponse is not used by toResponse(), so no stubbing required

        // Act
        WorkOrderPartResponse result = workOrderPartMapper.toResponse(workOrderPart);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.quantity());
    }

    @Test
    void shouldHandleWorkOrderPartWithDifferentQuantities() {
        // Arrange
        Part part = new Part();
        part.setId(UUID.randomUUID());

        WorkOrderPart workOrderPart1 = new WorkOrderPart(part.getId(), 1);
        workOrderPart1.setPart(part);

        WorkOrderPart workOrderPart2 = new WorkOrderPart(part.getId(), 100);
        workOrderPart2.setPart(part);

        // Act
        WorkOrderPartEntity result1 = workOrderPartMapper.toEntity(workOrderPart1);
        WorkOrderPartEntity result2 = workOrderPartMapper.toEntity(workOrderPart2);

        // Assert
        assertEquals(1, result1.getQuantity());
        assertEquals(100, result2.getQuantity());
    }

    @Test
    void shouldHandleNullPartInWorkOrderPart() {
        // Arrange
        WorkOrderPart workOrderPart = new WorkOrderPart(UUID.randomUUID(), 5);
        workOrderPart.setPart(null);
        workOrderPart.setQuantity(5);

        // Act
        WorkOrderPartEntity result = workOrderPartMapper.toEntity(workOrderPart);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
    }
}
