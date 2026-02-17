package com.fiap.gateway.messaging.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.workorder.WorkOrderStatus;
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

    @SqsListener("${aws.sqs.queue.stock-failed}")
    public void listenStockFailed(String message) {
        try {
            System.out.println("Evento Recebido: Estoque FALHOU. Payload: " + message);
            UUID workOrderId = extractWorkOrderId(message);
            updateStatusWorkOrderUseCase.execute(workOrderId, WorkOrderStatus.REFUSED_STOCK.name());
        } catch (Exception e) {
            System.err.println("Erro ao processar falha de estoque: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @SqsListener("${aws.sqs.queue.stock-approved}")
    public void listenStockApproved(String message) {
        try {
            System.out.println("Evento Recebido: Baixa de Estoque APROVADA. Payload: " + message);
            UUID workOrderId = extractWorkOrderId(message);

            updateStatusWorkOrderUseCase.execute(workOrderId, WorkOrderStatus.APPROVAL_STOCK.name());
        } catch (Exception e) {
            System.err.println("Erro ao processar baixa de estoque: " + e.getMessage());
            throw new RuntimeException(e);
        }
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