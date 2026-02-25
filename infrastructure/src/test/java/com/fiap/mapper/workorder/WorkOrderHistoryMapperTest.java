package com.fiap.mapper.workorder;

import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.dto.workorder.GetWorkOrderHistoryResponse;
import com.fiap.persistence.entity.workOrder.WorkOrderEntity;
import com.fiap.persistence.entity.workOrder.WorkOrderHistoryEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderHistoryMapperTest {

    @InjectMocks
    private WorkOrderHistoryMapper workOrderHistoryMapper;

    @Test
    void shouldMapWorkOrderHistoryToEntity() {
        UUID workOrderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        WorkOrderEntity workOrderEntity = new WorkOrderEntity();
        workOrderEntity.setId(workOrderId);

        WorkOrderHistory history = new WorkOrderHistory(
                workOrderId,
                null,
                WorkOrderStatus.IN_PROGRESS,
                "Nota de teste",
                now
        );

        WorkOrderHistoryEntity result = workOrderHistoryMapper.toEntity(history, workOrderEntity);

        assertNotNull(result);
        assertEquals(workOrderEntity, result.getWorkOrder());
        assertEquals(WorkOrderStatus.IN_PROGRESS, result.getStatus());
        assertEquals("Nota de teste", result.getNotes());
        assertEquals(now, result.getCreatedAt());
    }

    @Test
    void shouldMapWorkOrderHistoryEntityToDomain() {
        UUID workOrderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        WorkOrderEntity woEntity = new WorkOrderEntity();
        woEntity.setId(workOrderId);

        WorkOrderHistoryEntity entity = WorkOrderHistoryEntity.builder()
                .workOrder(woEntity)
                .status(WorkOrderStatus.COMPLETED)
                .notes("Finalizado")
                .createdAt(now)
                .build();

        WorkOrderHistory result = workOrderHistoryMapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(workOrderId, result.getWorkOrderId());
        assertEquals(WorkOrderStatus.COMPLETED, result.getStatus());
        assertEquals("Finalizado", result.getNotes());
        assertEquals(now, result.getCreatedAt());
    }

    @Test
    void shouldMapWorkOrderHistoryToResponse() {
        UUID workOrderId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        WorkOrderHistory history = new WorkOrderHistory(
                workOrderId,
                null,
                WorkOrderStatus.AWAITING_APPROVAL,
                "Aguardando",
                now
        );

        GetWorkOrderHistoryResponse result = workOrderHistoryMapper.toResponse(history);

        assertNotNull(result);
        assertEquals(workOrderId, result.workOrderId());
        assertEquals(WorkOrderStatus.AWAITING_APPROVAL.getDescription(), result.status());
        assertEquals("Aguardando", result.notes());
    }

    @Test
    void shouldMapListToDomain() {
        WorkOrderHistoryEntity entity = WorkOrderHistoryEntity.builder()
                .status(WorkOrderStatus.RECEIVED)
                .build();

        List<WorkOrderHistory> results = workOrderHistoryMapper.toDomain(List.of(entity));

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(WorkOrderStatus.RECEIVED, results.get(0).getStatus());
    }

    @Test
    void shouldMapFromWorkOrders() {
        UUID id = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        WorkOrderEntity wo = new WorkOrderEntity();
        wo.setId(id);
        wo.setCreatedAt(now);
        wo.setApprovedAt(now.plusHours(1));

        List<WorkOrderHistory> results = workOrderHistoryMapper.fromWorkOrders(List.of(wo));

        assertEquals(2, results.size());
        assertEquals(WorkOrderStatus.RECEIVED, results.get(0).getStatus());
        assertEquals(WorkOrderStatus.AWAITING_APPROVAL, results.get(1).getStatus());
    }

    @Test
    void shouldReturnNullWhenMappingNull() {
        assertNull(workOrderHistoryMapper.toDomain((WorkOrderHistoryEntity) null));
        assertNull(workOrderHistoryMapper.toResponse((WorkOrderHistory) null));
        assertNull(workOrderHistoryMapper.toEntity(null, null));
    }

    @Test
    void shouldReturnEmptyListWhenMappingNullOrEmptyLists() {
        assertTrue(workOrderHistoryMapper.toDomain((List<WorkOrderHistoryEntity>) null).isEmpty());
        assertTrue(workOrderHistoryMapper.toResponse((List<WorkOrderHistory>) null).isEmpty());
        assertTrue(workOrderHistoryMapper.fromWorkOrders(null).isEmpty());
    }
}