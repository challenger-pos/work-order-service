package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.part.Money;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.service.Service;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.core.domain.workorder.WorkOrderService;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.core.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemsWorkOrderUseCaseImplTest {

    WorkOrderGateway workOrderGateway = mock(WorkOrderGateway.class);
    PartGateway partGateway = mock(PartGateway.class);
    ServiceGateway serviceGateway = mock(ServiceGateway.class);
    WorkOrderQueueGateway workOrderQueueGateway = mock (WorkOrderQueueGateway.class);

    WorkOrder existingOrder = mock(WorkOrder.class);
    WorkOrder increaseOrder = mock(WorkOrder.class);
    WorkOrder savedOrder = mock(WorkOrder.class);

    //TODO removido por conta do stock
//    @Test
//    void shouldAddItemsRecalcReserveAndPersist() throws Exception {
//        UUID woId = UUID.randomUUID();
//        UUID pid1 = UUID.randomUUID();
//        UUID pid2 = UUID.randomUUID();
//        UUID sid1 = UUID.randomUUID();
//
//        WorkOrderPart incP1 = mock(WorkOrderPart.class);
//        WorkOrderPart incP2 = mock(WorkOrderPart.class);
//        when(incP1.getPartId()).thenReturn(pid1);
//        when(incP2.getPartId()).thenReturn(pid2);
//        when(incP1.getQuantity()).thenReturn(3);
//        when(incP2.getQuantity()).thenReturn(2);
//
//        WorkOrderService incS1 = mock(WorkOrderService.class);
//        when(incS1.getServiceId()).thenReturn(sid1);
//        when(incS1.getQuantity()).thenReturn(1);
//
//        when(increaseOrder.getWorkOrderParts()).thenReturn(List.of(incP1, incP2));
//        when(increaseOrder.getWorkOrderServices()).thenReturn(List.of(incS1));
//
//        Part p1 = mock(Part.class);
//        Part p2 = mock(Part.class);
//        when(p1.getId()).thenReturn(pid1);
//        when(p2.getId()).thenReturn(pid2);
//        when(p1.getPrice()).thenReturn(Money.of(new BigDecimal("10.00")));
//        when(p2.getPrice()).thenReturn(Money.of(new BigDecimal("5.00")));
//
//        Service s1 = mock(Service.class);
//        when(s1.getId()).thenReturn(sid1);
//        when(s1.getBasePrice()).thenReturn(new BigDecimal("100.00"));
//
//        List<WorkOrderPart> currentParts = new ArrayList<>();
//        List<WorkOrderService> currentSvcs = new ArrayList<>();
//        when(existingOrder.getWorkOrderParts()).thenReturn(currentParts);
//        when(existingOrder.getWorkOrderServices()).thenReturn(currentSvcs);
//
//        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(existingOrder));
//        when(partGateway.findByIds(List.of(pid1, pid2))).thenReturn(List.of(p1, p2));
//        when(serviceGateway.findByIds(List.of(sid1))).thenReturn(List.of(s1));
//        when(workOrderGateway.save(existingOrder)).thenReturn(savedOrder);
//
//        AddItemsWorkOrderUseCaseImpl useCase =
//                new AddItemsWorkOrderUseCaseImpl(workOrderGateway, partGateway, serviceGateway);
//
//        WorkOrder result = useCase.execute(woId, increaseOrder);
//
//        assertSame(savedOrder, result);
//        assertEquals(2, currentParts.size());
//        assertEquals(1, currentSvcs.size());
//
//        InOrder inOrder = inOrder(workOrderGateway, existingOrder, partGateway, serviceGateway);
//        inOrder.verify(workOrderGateway).findById(woId);
//        inOrder.verify(existingOrder).restoreStock();
//        inOrder.verify(partGateway).findByIds(List.of(pid1, pid2));
//        inOrder.verify(serviceGateway).findByIds(List.of(sid1));
//        inOrder.verify(existingOrder).recalculateTotal();
//        inOrder.verify(existingOrder).reserveParts();
//        inOrder.verify(partGateway).saveAll(List.of(p1, p2));
//        inOrder.verify(workOrderGateway).save(existingOrder);
//
//        verifyNoMoreInteractions(workOrderGateway, partGateway, serviceGateway);
//    }

    @Test
    void shouldThrowWhenWorkOrderNotFound() {
        UUID woId = UUID.randomUUID();
        when(workOrderGateway.findById(woId)).thenReturn(Optional.empty());

        AddItemsWorkOrderUseCaseImpl useCase =
                new AddItemsWorkOrderUseCaseImpl(workOrderGateway, partGateway, serviceGateway, workOrderQueueGateway);

        assertThrows(NotFoundException.class, () -> useCase.execute(woId, increaseOrder));

        verify(workOrderGateway).findById(woId);
        verifyNoMoreInteractions(workOrderGateway);
        verifyNoInteractions(partGateway, serviceGateway);
    }

    //TODO removido por conta do approveStock
//    @Test
//    void shouldPropagateBadRequestFromReserveParts() throws Exception {
//        UUID woId = UUID.randomUUID();
//
//        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(existingOrder));
//
//        when(increaseOrder.getWorkOrderParts()).thenReturn(List.of());
//        when(increaseOrder.getWorkOrderServices()).thenReturn(List.of());
//
//        when(partGateway.findByIds(List.of())).thenReturn(List.of());
//        when(serviceGateway.findByIds(List.of())).thenReturn(List.of());
//
//        List<WorkOrderPart> currentParts = new ArrayList<>();
//        List<WorkOrderService> currentSvcs = new ArrayList<>();
//        when(existingOrder.getWorkOrderParts()).thenReturn(currentParts);
//        when(existingOrder.getWorkOrderServices()).thenReturn(currentSvcs);
//
//        doThrow(new BadRequestException("x", "y")).when(existingOrder).reserveParts();
//
//        AddItemsWorkOrderUseCaseImpl useCase =
//                new AddItemsWorkOrderUseCaseImpl(workOrderGateway, partGateway, serviceGateway);
//
//        assertThrows(BadRequestException.class, () -> useCase.execute(woId, increaseOrder));
//
//        verify(workOrderGateway).findById(woId);
//        verify(existingOrder).restoreStock();
//        verify(partGateway).findByIds(List.of());
//        verify(serviceGateway).findByIds(List.of());
//        verify(existingOrder).recalculateTotal();
//        verify(existingOrder).reserveParts();
//
//        verifyNoMoreInteractions(workOrderGateway, partGateway, serviceGateway);
//    }


    //TODO removido por conta do approveStock
//    @Test
//    void shouldPropagateBusinessRuleFromRestoreStock() throws Exception {
//        UUID woId = UUID.randomUUID();
//        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(existingOrder));
//        doThrow(new BusinessRuleException("b", "c")).when(existingOrder).restoreStock();
//
//        AddItemsWorkOrderUseCaseImpl useCase =
//                new AddItemsWorkOrderUseCaseImpl(workOrderGateway, partGateway, serviceGateway);
//
//        assertThrows(BusinessRuleException.class, () -> useCase.execute(woId, increaseOrder));
//
//        verify(workOrderGateway).findById(woId);
//        verify(existingOrder).restoreStock();
//        verifyNoMoreInteractions(workOrderGateway, existingOrder);
//        verifyNoInteractions(partGateway, serviceGateway);
//    }
}
