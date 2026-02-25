package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.part.Money;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.service.Service;
import com.fiap.core.domain.workorder.*;
import com.fiap.core.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemsWorkOrderUseCaseImplTest {

    @Mock private WorkOrderGateway workOrderGateway;
    @Mock private PartGateway partGateway;
    @Mock private ServiceGateway serviceGateway;
    @Mock private WorkOrderQueueGateway workOrderQueueGateway;

    private AddItemsWorkOrderUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new AddItemsWorkOrderUseCaseImpl(
                workOrderGateway, partGateway, serviceGateway, workOrderQueueGateway
        );
    }

    @Test
    void shouldAddItemsRecalculateChangeStatusPublishAndSave() throws Exception {
        UUID workOrderId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();

        WorkOrder existingOrder = mock(WorkOrder.class);
        when(workOrderGateway.findById(workOrderId)).thenReturn(Optional.of(existingOrder));

        List<WorkOrderPart> currentParts = new ArrayList<>();
        List<WorkOrderService> currentServices = new ArrayList<>();
        when(existingOrder.getWorkOrderParts()).thenReturn(currentParts);
        when(existingOrder.getWorkOrderServices()).thenReturn(currentServices);

        WorkOrder increaseOrder = mock(WorkOrder.class);

        WorkOrderPart incPart = mock(WorkOrderPart.class);
        when(incPart.getPartId()).thenReturn(partId);
        when(incPart.getQuantity()).thenReturn(2);

        WorkOrderService incService = mock(WorkOrderService.class);
        when(incService.getServiceId()).thenReturn(serviceId);
        when(incService.getQuantity()).thenReturn(1);

        when(increaseOrder.getWorkOrderParts()).thenReturn(List.of(incPart));
        when(increaseOrder.getWorkOrderServices()).thenReturn(List.of(incService));

        Part part = mock(Part.class);
        when(part.getId()).thenReturn(partId);
        when(part.getPrice()).thenReturn(Money.of(new BigDecimal("50.00")));
        when(partGateway.findByIds(List.of(partId))).thenReturn(List.of(part));

        Service service = mock(Service.class);
        when(service.getId()).thenReturn(serviceId);
        when(service.getBasePrice()).thenReturn(new BigDecimal("150.00"));
        when(serviceGateway.findByIds(List.of(serviceId))).thenReturn(List.of(service));

        WorkOrder savedOrder = mock(WorkOrder.class);
        when(workOrderGateway.save(existingOrder)).thenReturn(savedOrder);

        WorkOrder result = useCase.execute(workOrderId, increaseOrder);

        assertSame(savedOrder, result);
        assertEquals(1, currentParts.size());
        assertEquals(1, currentServices.size());

        InOrder inOrder = inOrder(workOrderGateway, partGateway, serviceGateway, existingOrder, workOrderQueueGateway);
        inOrder.verify(workOrderGateway).findById(workOrderId);
        inOrder.verify(partGateway).findByIds(List.of(partId));
        inOrder.verify(serviceGateway).findByIds(List.of(serviceId));
        inOrder.verify(existingOrder).recalculateTotal();
        inOrder.verify(existingOrder).setStatus(WorkOrderStatus.AWAITING_STOCK_CONFIRMATION);
        inOrder.verify(workOrderQueueGateway).publishStockReservation(existingOrder);
        inOrder.verify(workOrderGateway).saveHistory(any(WorkOrderHistory.class));
        inOrder.verify(workOrderGateway).save(existingOrder);

        verifyNoMoreInteractions(workOrderGateway, partGateway, serviceGateway, workOrderQueueGateway);
    }

    @Test
    void shouldThrowWhenWorkOrderNotFound() {
        UUID workOrderId = UUID.randomUUID();
        WorkOrder increaseOrder = mock(WorkOrder.class);

        when(workOrderGateway.findById(workOrderId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(workOrderId, increaseOrder));

        verify(workOrderGateway).findById(workOrderId);
        verifyNoMoreInteractions(workOrderGateway);
        verifyNoInteractions(partGateway, serviceGateway, workOrderQueueGateway);
    }

    @Test
    void shouldAddOnlyPartsRecalculateChangeStatusAndSave() throws Exception {
        UUID workOrderId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();

        WorkOrder existingOrder = mock(WorkOrder.class);
        when(workOrderGateway.findById(workOrderId)).thenReturn(Optional.of(existingOrder));

        when(existingOrder.getWorkOrderParts()).thenReturn(new ArrayList<>());
        when(existingOrder.getWorkOrderServices()).thenReturn(new ArrayList<>());

        WorkOrder increaseOrder = mock(WorkOrder.class);

        WorkOrderPart incPart = mock(WorkOrderPart.class);
        when(incPart.getPartId()).thenReturn(partId);
        when(incPart.getQuantity()).thenReturn(2);

        when(increaseOrder.getWorkOrderParts()).thenReturn(List.of(incPart));
        when(increaseOrder.getWorkOrderServices()).thenReturn(List.of());

        Part part = mock(Part.class);
        when(part.getId()).thenReturn(partId);
        when(part.getPrice()).thenReturn(Money.of(new BigDecimal("50.00")));
        when(partGateway.findByIds(List.of(partId))).thenReturn(List.of(part));

        when(serviceGateway.findByIds(List.of())).thenReturn(List.of());

        when(workOrderGateway.save(existingOrder)).thenReturn(existingOrder);

        WorkOrder result = useCase.execute(workOrderId, increaseOrder);

        assertNotNull(result);
        verify(partGateway).findByIds(List.of(partId));
        verify(serviceGateway).findByIds(List.of());
        verify(existingOrder).recalculateTotal();
        verify(workOrderQueueGateway).publishStockReservation(existingOrder);
    }
}