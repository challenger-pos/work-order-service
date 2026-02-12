package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.customer.DocumentNumber;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.ForbiddenException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.UnauthorizedException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApproveWorkOrderUseCaseImplTest {

    @Mock
    WorkOrderGateway workOrderGateway;

    @Mock
    WorkOrderQueueGateway workOrderQueueGateway;

    @Mock
    PartGateway partGateway; // Mantido pois sua implementação ainda o injeta no construtor

    @Mock
    WorkOrder workOrder;

    @Mock
    Customer customer;

    @Mock
    DocumentNumber documentNumberObj;

    @InjectMocks
    ApproveWorkOrderUseCaseImpl useCase;

    @Test
    void shouldThrowNotFoundWhenWorkOrderDoesNotExist() {
        UUID id = UUID.randomUUID();
        String dummyDocument = "12345678900"; // Necessário passar algo não nulo

        when(workOrderGateway.findById(id)).thenReturn(Optional.empty());

        // CORREÇÃO: Passando o segundo argumento exigido
        assertThrows(NotFoundException.class, () -> useCase.execute(id, dummyDocument));

        verify(workOrderGateway).findById(id);
    }

    @Test
    void shouldThrowUnauthorizedWhenDocumentNumberIsNull() {
        UUID id = UUID.randomUUID();
        String documentNumber = null;

        // O erro é lançado antes de buscar no banco, então não precisa mockar o findById
        UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> useCase.execute(id, documentNumber));

        assertEquals(ErrorCodeEnum.WORK0008.getCode(), ex.getCode());
        verifyNoInteractions(workOrderGateway);
    }

    @Test
    void shouldThrowBadRequestWhenStatusIsNotAwaitingApproval() {
        UUID id = UUID.randomUUID();
        String dummyDocument = "12345678900";

        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.RECEIVED); // Status errado

        BadRequestException ex = assertThrows(BadRequestException.class, () -> useCase.execute(id, dummyDocument));

        assertEquals(ErrorCodeEnum.WORK0006.getCode(), ex.getCode());
        verify(workOrderGateway).findById(id);
    }

    @Test
    void shouldThrowForbiddenWhenDocumentNumberDoesNotMatch() {
        UUID id = UUID.randomUUID();
        String requestDocument = "11111111111"; // Documento vindo da requisição
        String actualDocument = "22222222222";  // Documento real do dono da OS

        // Mock da estrutura da OS -> Customer -> DocumentNumber
        when(workOrderGateway.findById(id)).thenReturn(Optional.of(workOrder));
        when(workOrder.getStatus()).thenReturn(WorkOrderStatus.AWAITING_APPROVAL);
        when(workOrder.getCustomer()).thenReturn(customer);
        when(customer.getDocumentNumber()).thenReturn(documentNumberObj);
        when(documentNumberObj.getValue()).thenReturn(actualDocument);

        ForbiddenException ex = assertThrows(ForbiddenException.class, () -> useCase.execute(id, requestDocument));

        assertEquals(ErrorCodeEnum.WORK0007.getCode(), ex.getCode());
    }

    // TODO verificar se teste está ok
//    @Test
//    void shouldApproveAndPublishStockReservation() throws ForbiddenException, UnauthorizedException, NotFoundException, BadRequestException {
//        // Cenario: Sucesso
//        UUID id = UUID.randomUUID();
//        String validDocument = "12345678900";
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
//        // 1. Mudança de Status
//        verify(workOrder).setStatus(WorkOrderStatus.APPROVAL_STOCK);
//        verify(workOrder).setApprovedAt(any());
//
//        // 2. Persistência
//        verify(workOrderGateway).save(workOrder);
//        verify(workOrderGateway).saveHistory(any(WorkOrderHistory.class));
//
//        // 3. Integração com Fila (Saga)
//        verify(workOrderQueueGateway).publishStockReservation(workOrder);
//    }
}