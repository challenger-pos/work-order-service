package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.user.UserGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.user.UserRole;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssignedMechanicUseCaseImplTest {

    @Mock
    WorkOrderGateway workOrderGateway;

    @Mock
    UserGateway userGateway;

    @Mock
    WorkOrder workOrder;

    @Mock
    User mechanic;

    @Test
    void shouldThrowWhenWorkOrderNotFound() {
        UUID woId = UUID.randomUUID();
        UUID mechId = UUID.randomUUID();
        when(workOrderGateway.findById(woId)).thenReturn(Optional.empty());

        AssignedMechanicUseCaseImpl useCase = new AssignedMechanicUseCaseImpl(workOrderGateway, userGateway);

        assertThrows(NotFoundException.class, () -> useCase.execute(woId, mechId));

        verify(workOrderGateway).findById(woId);
        verifyNoMoreInteractions(workOrderGateway, userGateway);
    }

    @Test
    void shouldThrowWhenMechanicNotFound() throws NotFoundException {
        UUID woId = UUID.randomUUID();
        UUID mechId = UUID.randomUUID();
        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(workOrder));
        when(userGateway.findById(mechId)).thenReturn(Optional.empty());

        AssignedMechanicUseCaseImpl useCase = new AssignedMechanicUseCaseImpl(workOrderGateway, userGateway);

        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute(woId, mechId));
        assert ex.getCode().equals(ErrorCodeEnum.USE0007.getCode());

        verify(workOrderGateway).findById(woId);
        verify(userGateway).findById(mechId);
        verifyNoMoreInteractions(workOrderGateway, userGateway);
    }

    @Test
    void shouldThrowWhenWorkOrderStatusNotReceived() throws Exception {
        UUID woId = UUID.randomUUID();
        UUID mechId = UUID.randomUUID();
        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(workOrder));
        when(userGateway.findById(mechId)).thenReturn(Optional.of(mechanic));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.IN_PROGRESS);

        AssignedMechanicUseCaseImpl useCase = new AssignedMechanicUseCaseImpl(workOrderGateway, userGateway);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(woId, mechId));
        assert ex.getCode().equals(ErrorCodeEnum.WORK0003.getCode());

        verify(workOrderGateway).findById(woId);
        verify(userGateway).findById(mechId);
        verify(workOrder).getStatus();
        verifyNoMoreInteractions(workOrderGateway, userGateway, workOrder, mechanic);
    }

    @Test
    void shouldThrowWhenUserNotMechanic() throws Exception {
        UUID woId = UUID.randomUUID();
        UUID mechId = UUID.randomUUID();
        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(workOrder));
        when(userGateway.findById(mechId)).thenReturn(Optional.of(mechanic));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.RECEIVED);
        when(mechanic.getRole()).thenReturn(UserRole.ADMIN);

        AssignedMechanicUseCaseImpl useCase = new AssignedMechanicUseCaseImpl(workOrderGateway, userGateway);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(woId, mechId));
        assert ex.getCode().equals(ErrorCodeEnum.USE0009.getCode());

        verify(workOrderGateway).findById(woId);
        verify(userGateway).findById(mechId);
        verify(workOrder).getStatus();
        verify(mechanic).getRole();
        verifyNoMoreInteractions(workOrderGateway, userGateway);
    }

    @Test
    void shouldAssignMechanicAndChangeStatus() throws Exception {
        UUID woId = UUID.randomUUID();
        UUID mechId = UUID.randomUUID();
        when(workOrderGateway.findById(woId)).thenReturn(Optional.of(workOrder));
        when(userGateway.findById(mechId)).thenReturn(Optional.of(mechanic));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.RECEIVED);
        when(mechanic.getRole()).thenReturn(UserRole.MECHANIC);

        AssignedMechanicUseCaseImpl useCase = new AssignedMechanicUseCaseImpl(workOrderGateway, userGateway);

        useCase.execute(woId, mechId);

        InOrder inOrder = inOrder(workOrderGateway, userGateway, workOrder, mechanic);
        inOrder.verify(workOrderGateway).findById(woId);
        inOrder.verify(userGateway).findById(mechId);
        inOrder.verify(workOrder).getStatus();
        inOrder.verify(mechanic).getRole();
        inOrder.verify(workOrder).setAssignedMechanic(mechanic);
        inOrder.verify(workOrder).setStatus(WorkOrderStatus.IN_DIAGNOSIS);
        inOrder.verify(workOrderGateway).save(workOrder);
        inOrder.verify(workOrderGateway).saveHistory(any(WorkOrderHistory.class));

        verifyNoMoreInteractions(workOrderGateway, userGateway);
    }
}
