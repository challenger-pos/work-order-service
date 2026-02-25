package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.customer.DocumentNumber;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.*;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefuseWorkOrderUseCaseImplTest {

    @Mock
    WorkOrderGateway workOrderGateway;

    @Mock
    WorkOrderQueueGateway workOrderQueueGateway; // Novo mock necessário

    @Mock
    PartGateway partGateway;

    @Mock
    WorkOrder workOrder;

    @Mock
    Customer customer;

    @Mock
    DocumentNumber documentNumberObj;

    @InjectMocks
    RefuseWorkOrderUseCaseImpl useCase;

    @Test
    void shouldThrowWhenWorkOrderNotFound() {
        UUID id = UUID.randomUUID();
        String documentNumber = "01782982043";

        when(workOrderGateway.findById(id)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(id, documentNumber));

        verify(workOrderGateway).findById(id);
    }

    @Test
    void shouldThrowWhenStatusIsNotAwaitingApproval() {
        UUID id = UUID.randomUUID();
        String documentNumber = "01782982043";

        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.IN_PROGRESS);

        assertThrows(BadRequestException.class, () -> useCase.execute(id, documentNumber));

        verify(workOrderGateway).findById(id);
    }

//    @Test
//    void shouldRefuseAndPublishCancellation() throws BusinessRuleException, ForbiddenException, UnauthorizedException, NotFoundException, BadRequestException {
//        // Cenário de Sucesso
//        UUID id = UUID.randomUUID();
//        String validDocument = "01782982043";
//
//        // Mocks de comportamento
//        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
//        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.AWAITING_APPROVAL);
//
//        // Mock da validação de documento
//        when(workOrder.getCustomer()).thenReturn(customer);
//        when(customer.getDocumentNumber()).thenReturn(documentNumberObj);
//        when(documentNumberObj.getValue()).thenReturn(validDocument);
//
//        // Mock para evitar NullPointerException no log (workOrder.getWorkOrderParts().size())
//        when(workOrder.getWorkOrderParts()).thenReturn(new ArrayList<>());
//        when(workOrder.getId()).thenReturn(id);
//
//        // Execução
//        useCase.execute(id, validDocument);
//
//        // Verificações
//
//        // 1. Deve alterar o status para REFUSED
//        verify(workOrder).setStatus(WorkOrderStatus.REFUSED);
//        verify(workOrder).setFinishedAt(any(LocalDateTime.class));
//
//        // 2. Deve salvar a OS atualizada
//        verify(workOrderGateway).save(workOrder);
//
//        // 3. CRÍTICO: Deve enviar o evento de cancelamento para a fila
//        verify(workOrderQueueGateway).publishStockCancellation(workOrder);
//
//        // 4. Deve salvar o histórico
//        verify(workOrderGateway).saveHistory(any(WorkOrderHistory.class));
//    }

    @Test
    void shouldThrowForbiddenWhenDocumentNumberDoesNotMatchWorkOrder() {
        UUID id = UUID.randomUUID();
        String requestDocumentNumber = "01782982043"; // CPF da requisição
        String workOrderDocumentNumber = "12345678900"; // CPF do dono da OS

        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.AWAITING_APPROVAL);

        when(workOrder.getCustomer()).thenReturn(customer);
        when(customer.getDocumentNumber()).thenReturn(documentNumberObj);
        when(documentNumberObj.getValue()).thenReturn(workOrderDocumentNumber);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute(id, requestDocumentNumber));
        assertEquals(ErrorCodeEnum.WORK0007.getCode(), ex.getCode());

        verify(workOrderGateway).findById(id);
    }

    @Test
    void shouldThrowUnauthorizedWhenDocumentNumberIsNull() {
        UUID id = UUID.randomUUID();
        String documentNumber = null;

        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(id, documentNumber));
        assertEquals(ErrorCodeEnum.WORK0008.getCode(), ex.getCode());

        verifyNoInteractions(workOrderGateway);
    }
}