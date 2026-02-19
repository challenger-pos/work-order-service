package com.fiap.gateway.workorder;

import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.mapper.workorder.WorkOrderHistoryMapper;
import com.fiap.mapper.workorder.WorkOrderMapper;
import com.fiap.persistence.entity.workOrder.WorkOrderEntity;
import com.fiap.persistence.entity.workOrder.WorkOrderHistoryEntity;
import com.fiap.persistence.repository.workorder.WorkOrderRepository;
import com.fiap.persistence.repository.workorder.WorkOrderHistoryRepository;
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
class WorkOrderRepositoryGatewayTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private WorkOrderHistoryRepository workOrderHistoryRepository;

    @Mock
    private WorkOrderMapper workOrderMapper;

    @Mock
    private WorkOrderHistoryMapper workOrderHistoryMapper;

    @InjectMocks
    private WorkOrderRepositoryGateway workOrderRepositoryGateway;

    @Test
    void shouldSaveWorkOrderSuccessfully() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(workOrderId);

        WorkOrderEntity workOrderEntity = new WorkOrderEntity();
        workOrderEntity.setId(workOrderId);

        when(workOrderMapper.toEntity(workOrder)).thenReturn(workOrderEntity);
        when(workOrderRepository.save(workOrderEntity)).thenReturn(workOrderEntity);
        when(workOrderMapper.toDomain(workOrderEntity)).thenReturn(workOrder);

        // Act
        WorkOrder result = workOrderRepositoryGateway.save(workOrder);

        // Assert
        assertNotNull(result);
        assertEquals(workOrderId, result.getId());
        verify(workOrderRepository).save(workOrderEntity);
    }

    @Test
    void shouldFindWorkOrderByIdSuccessfully() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(workOrderId);

        WorkOrderEntity workOrderEntity = new WorkOrderEntity();
        workOrderEntity.setId(workOrderId);

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrderEntity));
        when(workOrderMapper.toDomain(workOrderEntity)).thenReturn(workOrder);

        // Act
        Optional<WorkOrder> result = workOrderRepositoryGateway.findById(workOrderId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(workOrderId, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenWorkOrderNotFound() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.empty());

        // Act
        Optional<WorkOrder> result = workOrderRepositoryGateway.findById(workOrderId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldUpdateWorkOrderSuccessfully() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(workOrderId);

        WorkOrderEntity workOrderEntity = new WorkOrderEntity();
        workOrderEntity.setId(workOrderId);

        when(workOrderMapper.toEntity(workOrder)).thenReturn(workOrderEntity);
        when(workOrderRepository.save(workOrderEntity)).thenReturn(workOrderEntity);
        when(workOrderMapper.toDomain(workOrderEntity)).thenReturn(workOrder);

        // Act
        WorkOrder result = workOrderRepositoryGateway.update(workOrder);

        // Assert
        assertNotNull(result);
        verify(workOrderRepository).save(workOrderEntity);
    }

    @Test
    void shouldFindWorkOrdersByStatus() {
        // Arrange
        WorkOrderStatus status = WorkOrderStatus.IN_PROGRESS;
        WorkOrder workOrder1 = new WorkOrder();
        WorkOrder workOrder2 = new WorkOrder();

        WorkOrderEntity entity1 = new WorkOrderEntity();
        WorkOrderEntity entity2 = new WorkOrderEntity();

        when(workOrderRepository.findByStatus(status)).thenReturn(Arrays.asList(entity1, entity2));
        when(workOrderMapper.toDomain(entity1)).thenReturn(workOrder1);
        when(workOrderMapper.toDomain(entity2)).thenReturn(workOrder2);

        // Act
        List<WorkOrder> results = workOrderRepositoryGateway.findByStatus(status);

        // Assert
        assertEquals(2, results.size());
        verify(workOrderRepository).findByStatus(status);
    }

    @Test
    void shouldFindAllOrderedByStatus() {
        // Arrange
        List<WorkOrderStatus> statuses = Arrays.asList(WorkOrderStatus.RECEIVED, WorkOrderStatus.IN_PROGRESS);
        WorkOrder workOrder = new WorkOrder();

        WorkOrderEntity entity = new WorkOrderEntity();

        when(workOrderRepository.findAllOrdered(statuses)).thenReturn(Arrays.asList(entity));
        when(workOrderMapper.toDomain(entity)).thenReturn(workOrder);

        // Act
        List<WorkOrder> results = workOrderRepositoryGateway.findAllOrdered(statuses);

        // Assert
        assertEquals(1, results.size());
        verify(workOrderRepository).findAllOrdered(statuses);
    }

    @Test
    void shouldSaveWorkOrderHistorySuccessfully() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        WorkOrderHistory history = new WorkOrderHistory();
        history.setWorkOrderId(workOrderId);

        WorkOrderEntity workOrderEntity = new WorkOrderEntity();
        workOrderEntity.setId(workOrderId);

        WorkOrderHistoryEntity historyEntity = new WorkOrderHistoryEntity();

        when(workOrderRepository.findById(workOrderId)).thenReturn(Optional.of(workOrderEntity));
        when(workOrderHistoryMapper.toEntity(history, workOrderEntity)).thenReturn(historyEntity);
        when(workOrderHistoryRepository.save(historyEntity)).thenReturn(historyEntity);

        // Act
        workOrderRepositoryGateway.saveHistory(history);

        // Assert
        verify(workOrderRepository).findById(workOrderId);
        verify(workOrderHistoryRepository).save(historyEntity);
    }

    @Test
    void shouldNotSaveHistoryWhenHistoryIsNull() {
        // Act
        workOrderRepositoryGateway.saveHistory(null);

        // Assert
        verify(workOrderRepository, never()).findById(any());
    }

    @Test
    void shouldGetHistoryByCustomerDocument() {
        // Arrange
        String cpfCnpj = "123.456.789-00";
        String cleaned = "12345678900";

        WorkOrderEntity entity = new WorkOrderEntity();
        WorkOrderHistory history = new WorkOrderHistory();

        when(workOrderRepository.findByCustomerDocumentNumberOrderByCreatedAtAsc(cleaned))
                .thenReturn(Arrays.asList(entity));
        when(workOrderHistoryMapper.fromWorkOrders(Arrays.asList(entity)))
                .thenReturn(Arrays.asList(history));

        // Act
        List<WorkOrderHistory> results = workOrderRepositoryGateway.getHistoryByCustomerCpfCnpj(cpfCnpj);

        // Assert
        assertEquals(1, results.size());
        verify(workOrderRepository).findByCustomerDocumentNumberOrderByCreatedAtAsc(cleaned);
    }
}
