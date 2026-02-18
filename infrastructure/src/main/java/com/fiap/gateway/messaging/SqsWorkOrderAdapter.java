package com.fiap.gateway.messaging;

import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.gateway.messaging.dto.*;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class SqsWorkOrderAdapter implements WorkOrderQueueGateway {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue.stock-request}")
    private String stockRequestQueue;

    @Value("${aws.sqs.queue.stock-cancel}")
    private String stockCancelQueue;

    @Value("${aws.sqs.queue.payment-request}")
    private String paymentRequestQueue;

    @Value("${aws.sqs.queue.stock-approved}")
    private String stockApprovedQueue;

    public SqsWorkOrderAdapter(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    @Override
    public void publishStockReservation(WorkOrder workOrder) {
        List<StockReservationItem> items = workOrder.getWorkOrderParts().stream()
                .map(part -> new StockReservationItem(part.getPart().getId(), part.getQuantity()))
                .collect(Collectors.toList());

        StockReservationEvent event = new StockReservationEvent(workOrder.getId().toString(), items);

        System.out.println("Enviando comando de RESERVA para OS: " + workOrder.getId() + " na fila " + stockRequestQueue);
        sqsTemplate.send(stockRequestQueue, event);
    }

    @Override
    public void publishStockCancellation(WorkOrder workOrder) {
        List<StockCancelItem> items = workOrder.getWorkOrderParts().stream()
                .map(part -> new StockCancelItem(part.getPart().getId(), part.getQuantity()))
                .collect(Collectors.toList());

        StockCancelRequestedEvent event = new StockCancelRequestedEvent(workOrder.getId(), items);

        System.out.println("Enviando comando de CANCELAMENTO DE ESTOQUE para OS: " + workOrder.getId() + " na fila " + stockCancelQueue);

        sqsTemplate.send(stockCancelQueue, event);
    }

    @Override
    public void publishPaymentRequest(WorkOrder workOrder) {
        PaymentRequestEvent event = new PaymentRequestEvent(
                workOrder.getId().toString(),
                workOrder.getCustomer().getId().toString(),
                workOrder.getTotalAmount()
        );

        System.out.println("Enviando solicitação de PAGAMENTO para OS: " + workOrder.getId() + " do Cliente: " + workOrder.getCustomer().getId() + " na fila " + paymentRequestQueue);
        sqsTemplate.send(paymentRequestQueue, event);
    }

    @Override
    public void publishStockDecrease(WorkOrder workOrder) {
        List<StockApprovedItem> items = workOrder.getWorkOrderParts().stream()
                .map(p -> new StockApprovedItem(p.getPart().getId(), p.getQuantity()))
                .collect(Collectors.toList());

        StockApprovedEvent event = new StockApprovedEvent(workOrder.getId(), items);

        System.out.println("Enviando EFETIVAÇÃO DE BAIXA para Stock: " + workOrder.getId());
        sqsTemplate.send(stockApprovedQueue, event);
    }
}