package com.fiap.mapper.workorder;

import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.dto.workorder.WorkOrderResponse;
import com.fiap.mapper.customer.CustomerMapper;
import com.fiap.mapper.user.UserMapper;
import com.fiap.mapper.vehicle.VehicleMapper;
import com.fiap.persistence.entity.workOrder.WorkOrderEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WorkOrderMapperTest {

    @Mock
    private CustomerMapper customerMapper;

    @Mock
    private VehicleMapper vehicleMapper;

    @Mock
    private UserMapper userMapper;

    @Mock
    private WorkOrderServiceMapper workOrderServiceMapper;

    @Mock
    private WorkOrderPartMapper workOrderPartMapper;

    @InjectMocks
    private WorkOrderMapper workOrderMapper;

    @Test
    void shouldMapWorkOrderToEntity() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();

        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(workOrderId);
        workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
        workOrder.setWorkOrderParts(new ArrayList<>());
        workOrder.setWorkOrderServices(new ArrayList<>());

        // Act
        WorkOrderEntity result = workOrderMapper.toEntity(workOrder);

        // Assert
        assertNotNull(result);
        assertEquals(workOrderId, result.getId());
        assertEquals(WorkOrderStatus.IN_PROGRESS, result.getStatus());
    }

    @Test
    void shouldMapWorkOrderEntityToDomain() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(workOrderId);
        entity.setStatus(WorkOrderStatus.COMPLETED);
        entity.setWorkOrderPartEntities(new java.util.ArrayList<>());
        entity.setWorkOrderServiceEntities(new java.util.ArrayList<>());
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // Act
        WorkOrder result = workOrderMapper.toDomain(entity);

        // Assert
        assertNotNull(result);
        assertEquals(workOrderId, result.getId());
        assertEquals(WorkOrderStatus.COMPLETED, result.getStatus());
    }

    @Test
    void shouldHandleWorkOrderWithoutAssignedMechanic() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();

        WorkOrder workOrder = new WorkOrder();
        workOrder.setId(workOrderId);
        workOrder.setAssignedMechanic(null);
        workOrder.setWorkOrderParts(new ArrayList<>());
        workOrder.setWorkOrderServices(new ArrayList<>());

        // Act
        WorkOrderEntity result = workOrderMapper.toEntity(workOrder);

        // Assert
        assertNotNull(result);
        assertNull(result.getAssignedMechanic());
    }

    @Test
    void shouldMapWorkOrderWithDifferentStatuses() {
        // Arrange
        UUID workOrderId = UUID.randomUUID();

        WorkOrderEntity entity = new WorkOrderEntity();
        entity.setId(workOrderId);
        entity.setStatus(WorkOrderStatus.RECEIVED);
        entity.setWorkOrderPartEntities(new java.util.ArrayList<>());
        entity.setWorkOrderServiceEntities(new java.util.ArrayList<>());

        // Act
        WorkOrder result1 = workOrderMapper.toDomain(entity);

        entity.setStatus(WorkOrderStatus.DELIVERED);
        WorkOrder result2 = workOrderMapper.toDomain(entity);

        // Assert
        assertEquals(WorkOrderStatus.RECEIVED, result1.getStatus());
        assertEquals(WorkOrderStatus.DELIVERED, result2.getStatus());
    }
}
