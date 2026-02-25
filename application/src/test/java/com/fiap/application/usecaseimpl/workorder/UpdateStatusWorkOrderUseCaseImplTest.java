package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateStatusWorkOrderUseCaseImplTest {

    @Mock
    WorkOrderGateway workOrderGateway;

    @Mock
    ServiceGateway serviceGateway;

    @Mock
    WorkOrderQueueGateway workOrderQueueGateway; // Novo Mock

    @Mock
    WorkOrder workOrder;

    @Mock
    WorkOrder updated;

    @InjectMocks
    UpdateStatusWorkOrderUseCaseImpl useCase;

    @Test
    void shouldThrowNotFoundWhenWorkOrderMissing() {
        UUID id = UUID.randomUUID();
        when(workOrderGateway.findById(id)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class, () -> useCase.execute(id, "IN_PROGRESS"));
        assertEquals(ErrorCodeEnum.WORK0001.getCode(), ex.getCode());

        verify(workOrderGateway).findById(id);
    }

    @Test
    void shouldThrowBadRequestWhenStatusStringInvalid() {
        UUID id = UUID.randomUUID();
        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(id, "INVALID_STATUS"));
        assertEquals(ErrorCodeEnum.WORK0004.getCode(), ex.getCode());

        verify(workOrderGateway).findById(id);
    }

    @Test
    void shouldThrowBadRequestWhenNewStatusEqualsCurrent() {
        UUID id = UUID.randomUUID();
        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.IN_PROGRESS);

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(id, "IN_PROGRESS"));
        assertEquals(ErrorCodeEnum.WORK0005.getCode(), ex.getCode());

        verify(workOrderGateway).findById(id);
    }

    // TODO assim que a requisição estiver ok, descomentar
//    @Test
//    void shouldTriggerStockDecreaseWhenStatusIsInProgress() throws NotFoundException, BadRequestException {
//        // Cenário: Mudança para IN_PROGRESS deve disparar baixa de estoque
//        UUID id = UUID.randomUUID();
//
//        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
//        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.APPROVAL_STOCK); // Status anterior
//        when(workOrderGateway.update(any())).thenReturn(updated);
//
//        // Execução
//        WorkOrder result = useCase.execute(id, "IN_PROGRESS");
//
//        assertSame(updated, result);
//
//        // Verificações
//        verify(workOrder).setStatus(WorkOrderStatus.IN_PROGRESS);
//        verify(workOrderQueueGateway).publishStockDecrease(workOrder); // Verifica chamada da fila
//        verify(workOrderGateway).saveHistory(any(WorkOrderHistory.class));
//    }

    // TODO assim que a requisição estiver ok, descomentar
//    @Test
//    void shouldTriggerPaymentRequestAndSetFinishedAtWhenCompleted() throws NotFoundException, BadRequestException {
//        // Cenário: Mudança para COMPLETED deve disparar pagamento
//        UUID id = UUID.randomUUID();
//
//        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
//        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.IN_PROGRESS);
//        when(workOrderGateway.update(any())).thenReturn(updated);
//
//        // Execução
//        WorkOrder result = useCase.execute(id, "COMPLETED");
//
//        assertSame(updated, result);
//
//        // Verificações
//        verify(workOrder).setFinishedAt(any(LocalDateTime.class));
//        verify(workOrder).setStatus(WorkOrderStatus.COMPLETED);
//        verify(workOrderQueueGateway).publishPaymentRequest(workOrder); // Verifica chamada da fila de pagamento
//        verify(workOrderGateway).update(workOrder);
//    }

    // TODO  assim que a requisição estiver ok, descomentar
//    @Test
//    void shouldSetFinishedAtWhenDeliveredAndPersist() throws NotFoundException, BadRequestException {
//        UUID id = UUID.randomUUID();
//
//        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
//        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.APPROVAL_PAYMENT); // Exemplo de anterior
//        when(workOrderGateway.update(any())).thenReturn(updated);
//
//        WorkOrder result = useCase.execute(id, "DELIVERED");
//
//        assertSame(updated, result);
//
//        verify(workOrder).setFinishedAt(any(LocalDateTime.class));
//        verify(workOrder).setStatus(WorkOrderStatus.DELIVERED);
//        verify(workOrderGateway).update(workOrder);
//
//        // Garante que para DELIVERED não chamamos filas de pagamento nem estoque
//        verify(workOrderQueueGateway, never()).publishPaymentRequest(any());
//        verify(workOrderQueueGateway, never()).publishStockDecrease(any());
//    }

    @Test
    void shouldUpdateWithoutFinishedAtForNonTerminalStatuses() throws NotFoundException, BadRequestException {
        UUID id = UUID.randomUUID();

        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.RECEIVED);
        when(workOrderGateway.update(any())).thenReturn(updated);

        WorkOrder result = useCase.execute(id, "IN_DIAGNOSIS");

        assertSame(updated, result);

        verify(workOrder, never()).setFinishedAt(any(LocalDateTime.class));
        verify(workOrder).setStatus(WorkOrderStatus.IN_DIAGNOSIS);
        verify(workOrderGateway).update(workOrder);
    }
}