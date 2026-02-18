package com.fiap.gateway.messaging.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.events.StockFailedEvent;
import com.fiap.core.events.StockReservedEvent;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.usecase.workorder.UpdateStatusWorkOrderUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WorkOrderSqsListener {

    private final UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase;
    private final ObjectMapper objectMapper;

    public WorkOrderSqsListener(UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase, ObjectMapper objectMapper) {
        this.updateStatusWorkOrderUseCase = updateStatusWorkOrderUseCase;
        this.objectMapper = objectMapper;
    }

    @SqsListener("${aws.sqs.queue.stock-reserved}")
    public void listenStockApproved(StockReservedEvent event) throws NotFoundException, BadRequestException {
        System.out.println("Evento Recebido: Baixa de Estoque APROVADA. OS: " + event.workOrderId());

        updateStatusWorkOrderUseCase.execute(event.workOrderId(), WorkOrderStatus.AWAITING_APPROVAL.name());
    }

    @SqsListener("${aws.sqs.queue.stock-failed}")
    public void listenStockFailed(StockFailedEvent event) throws NotFoundException, BadRequestException {
        System.out.println("Evento Recebido: Estoque FALHOU. OS: " + event.workOrderId() + " Motivo: " + event.reason());

        updateStatusWorkOrderUseCase.execute(event.workOrderId(), WorkOrderStatus.REFUSED_STOCK.name());
    }

    @SqsListener("${aws.sqs.queue.payment-success}")
    public void listenPaymentSuccess(String message) {
        try {
            System.out.println("Evento Recebido: Pagamento com SUCESSO. Payload: " + message);
            UUID workOrderId = extractWorkOrderId(message);

            updateStatusWorkOrderUseCase.execute(workOrderId, WorkOrderStatus.APPROVAL_PAYMENT.name());
        } catch (Exception e) {
            System.err.println("Erro ao processar sucesso de pagamento: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SqsListener("${aws.sqs.queue.payment-failure}")
    public void listenPaymentFailure(String message) {
        try {
            System.out.println("Evento Recebido: Pagamento FALHOU. Payload: " + message);
            UUID workOrderId = extractWorkOrderId(message);

            updateStatusWorkOrderUseCase.execute(workOrderId, WorkOrderStatus.REFUSED_PAYMENT.name());
        } catch (Exception e) {
            System.err.println("Erro ao processar falha de pagamento: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private UUID extractWorkOrderId(String payload) throws Exception {
        JsonNode node = objectMapper.readTree(payload);
        return UUID.fromString(node.get("workOrderId").asText());
    }
}