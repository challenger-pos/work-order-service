package com.fiap.mapper.workorder;

import com.fiap.core.domain.service.Service;
import com.fiap.core.domain.workorder.WorkOrderService;
import com.fiap.dto.workorder.WorkOrderServiceResponse;
import com.fiap.mapper.service.ServiceMapper;
import com.fiap.persistence.entity.workOrder.WorkOrderServiceEntity;
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
class WorkOrderServiceMapperTest {

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private WorkOrderServiceMapper workOrderServiceMapper;

    @Test
    void shouldMapWorkOrderServiceToEntity() {
        UUID serviceId = UUID.randomUUID();
        Service service = new Service();
        service.setId(serviceId);

        WorkOrderService workOrderService = new WorkOrderService(serviceId, 1);
        workOrderService.setService(service);

        com.fiap.persistence.entity.service.ServiceEntity mockServiceEntity = new com.fiap.persistence.entity.service.ServiceEntity();

        when(serviceMapper.toEntity(service)).thenReturn(mockServiceEntity);

        WorkOrderServiceEntity result = workOrderServiceMapper.toEntity(workOrderService);

        assertNotNull(result);
        assertNull(result.getId());
        assertEquals(mockServiceEntity, result.getService());
        assertEquals(1, result.getQuantity());
    }

    @Test
    void shouldMapWorkOrderServiceEntityToDomain() {
        UUID serviceId = UUID.randomUUID();
        UUID workOrderServiceId = UUID.randomUUID();

        Service service = new Service();
        service.setId(serviceId);

        WorkOrderServiceEntity entity = new WorkOrderServiceEntity();
        entity.setId(workOrderServiceId);
        entity.setQuantity(5);

        com.fiap.persistence.entity.service.ServiceEntity serviceEntity = new com.fiap.persistence.entity.service.ServiceEntity();
        serviceEntity.setId(serviceId);
        entity.setService(serviceEntity);

        when(serviceMapper.toDomain(any(com.fiap.persistence.entity.service.ServiceEntity.class))).thenReturn(service);

        WorkOrderService result = workOrderServiceMapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(serviceId, result.getServiceId());
        assertEquals(5, result.getQuantity());
    }

    @Test
    void shouldMapWorkOrderServiceToResponse() {
        // Arrange
        UUID serviceId = UUID.randomUUID();
        Service service = new Service();
        service.setId(serviceId);
        service.setName("Service Test");

        WorkOrderService workOrderService = new WorkOrderService(serviceId, 1);
        workOrderService.setService(service);

        WorkOrderServiceResponse result = workOrderServiceMapper.toResponse(workOrderService);

        assertNotNull(result);
        assertEquals("Service Test", result.name());
        assertEquals(1, result.quantity());
    }

    @Test
    void shouldHandleNullServiceInWorkOrderService() {
        WorkOrderService workOrderService = new WorkOrderService(UUID.randomUUID(), 1);
        workOrderService.setService(null);
        WorkOrderServiceEntity result = workOrderServiceMapper.toEntity(workOrderService);

        assertNotNull(result);
        assertNull(result.getId());
        assertNull(result.getService());
        assertEquals(1, result.getQuantity());
    }

    @Test
    void shouldMapMultipleWorkOrderServices() {
        // Arrange
        UUID serviceId1 = UUID.randomUUID();
        UUID serviceId2 = UUID.randomUUID();

        Service service1 = new Service();
        service1.setId(serviceId1);

        Service service2 = new Service();
        service2.setId(serviceId2);

        WorkOrderService workOrderService1 = new WorkOrderService(serviceId1, 1);
        workOrderService1.setService(service1);

        WorkOrderService workOrderService2 = new WorkOrderService(serviceId2, 2);
        workOrderService2.setService(service2);

        com.fiap.persistence.entity.service.ServiceEntity entity1 = new com.fiap.persistence.entity.service.ServiceEntity();
        com.fiap.persistence.entity.service.ServiceEntity entity2 = new com.fiap.persistence.entity.service.ServiceEntity();

        when(serviceMapper.toEntity(service1)).thenReturn(entity1);
        when(serviceMapper.toEntity(service2)).thenReturn(entity2);

        WorkOrderServiceEntity result1 = workOrderServiceMapper.toEntity(workOrderService1);
        WorkOrderServiceEntity result2 = workOrderServiceMapper.toEntity(workOrderService2);

        assertNotNull(result1);
        assertNotNull(result2);

        assertEquals(entity1, result1.getService());
        assertEquals(entity2, result2.getService());
        assertEquals(1, result1.getQuantity());
        assertEquals(2, result2.getQuantity());
    }
}