package com.fiap.gateway.messaging.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.events.StockFailedEvent;
import com.fiap.core.events.StockReservedEvent;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.usecase.workorder.UpdateStatusWorkOrderUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderSqsListenerTest {

    @Mock
    private UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private WorkOrderSqsListener listener;

    @Test
    void shouldListenStockApprovedEvent() throws NotFoundException, BadRequestException {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        StockReservedEvent event = new StockReservedEvent(workOrderId);

        // Act
        listener.listenStockApproved(event);

        // Assert
        verify(updateStatusWorkOrderUseCase).execute(workOrderId, WorkOrderStatus.AWAITING_APPROVAL.name());
    }

    @Test
    void shouldListenStockFailedEvent() throws NotFoundException, BadRequestException {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        StockFailedEvent event = new StockFailedEvent(workOrderId, "Item out of stock");

        // Act
        listener.listenStockFailed(event);

        // Assert
        verify(updateStatusWorkOrderUseCase).execute(workOrderId, WorkOrderStatus.REFUSED_STOCK.name());
    }

    @Test
    void shouldListenPaymentSuccessMessage() throws Exception {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        String payload = "{\"workOrderId\": \"" + workOrderId.toString() + "\"}";

        when(objectMapper.readTree(payload)).thenReturn(
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)
        );

        // Act
        listener.listenPaymentSuccess(payload);

        // Assert
        verify(updateStatusWorkOrderUseCase).execute(workOrderId, WorkOrderStatus.APPROVAL_PAYMENT.name());
    }

    @Test
    void shouldListenPaymentFailureMessage() throws Exception {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        String payload = "{\"workOrderId\": \"" + workOrderId.toString() + "\"}";

        when(objectMapper.readTree(payload)).thenReturn(
                new com.fasterxml.jackson.databind.ObjectMapper().readTree(payload)
        );

        // Act
        listener.listenPaymentFailure(payload);

        // Assert
        verify(updateStatusWorkOrderUseCase).execute(workOrderId, WorkOrderStatus.REFUSED_PAYMENT.name());
    }

    @Test
    void shouldHandleStockApprovedEventWithException() throws NotFoundException, BadRequestException {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        StockReservedEvent event = new StockReservedEvent(workOrderId);
        doThrow(new NotFoundException("Work order not found", "WORK_ORDER_NOT_FOUND"))
                .when(updateStatusWorkOrderUseCase).execute(workOrderId, WorkOrderStatus.AWAITING_APPROVAL.name());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> listener.listenStockApproved(event));
    }

    @Test
    void shouldHandleStockFailedEventWithException() throws NotFoundException, BadRequestException {
        // Arrange
        UUID workOrderId = UUID.randomUUID();
        StockFailedEvent event = new StockFailedEvent(workOrderId, "Item out of stock");
        doThrow(new NotFoundException("Work order not found", "WORK_ORDER_NOT_FOUND"))
                .when(updateStatusWorkOrderUseCase).execute(workOrderId, WorkOrderStatus.REFUSED_STOCK.name());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> listener.listenStockFailed(event));
    }

    @Test
    void shouldHandlePaymentSuccessExtractionError() throws Exception {
        // Arrange
        String invalidPayload = "invalid json";
        when(objectMapper.readTree(invalidPayload)).thenThrow(new RuntimeException("JSON parsing error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listener.listenPaymentSuccess(invalidPayload));
    }

    @Test
    void shouldHandlePaymentFailureExtractionError() throws Exception {
        // Arrange
        String invalidPayload = "invalid json";
        when(objectMapper.readTree(invalidPayload)).thenThrow(new RuntimeException("JSON parsing error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> listener.listenPaymentFailure(invalidPayload));
    }

    @Test
    void shouldHandlePaymentSuccessWithMultipleWorkOrders() throws Exception {
        // Arrange
        UUID workOrderId1 = UUID.randomUUID();
        UUID workOrderId2 = UUID.randomUUID();
        String payload1 = "{\"workOrderId\": \"" + workOrderId1.toString() + "\"}";
        String payload2 = "{\"workOrderId\": \"" + workOrderId2.toString() + "\"}";

        ObjectMapper realObjectMapper = new ObjectMapper();
        when(objectMapper.readTree(payload1)).thenReturn(realObjectMapper.readTree(payload1));
        when(objectMapper.readTree(payload2)).thenReturn(realObjectMapper.readTree(payload2));

        // Act
        listener.listenPaymentSuccess(payload1);
        listener.listenPaymentSuccess(payload2);

        // Assert
        verify(updateStatusWorkOrderUseCase).execute(workOrderId1, WorkOrderStatus.APPROVAL_PAYMENT.name());
        verify(updateStatusWorkOrderUseCase).execute(workOrderId2, WorkOrderStatus.APPROVAL_PAYMENT.name());
    }
}
