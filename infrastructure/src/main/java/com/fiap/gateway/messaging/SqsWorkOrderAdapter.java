package com.fiap.gateway.messaging;

import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.gateway.messaging.dto.*;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SqsWorkOrderAdapter implements WorkOrderQueueGateway {

    private final SqsTemplate sqsTemplate;

    @Value("${aws.sqs.queue.stock-commands}")
    private String stockQueueUrl;

    @Value("${aws.sqs.queue.payment-commands}")
    private String paymentQueueUrl;

    @Override
    public void publishStockReservation(WorkOrder workOrder) {
        List<StockReservationItem> itemsDto = workOrder.getWorkOrderParts().stream()
                .map(part -> new StockReservationItem(
                        part.getPartId(),
                        part.getQuantity()
                ))
                .toList();

        StockReservationEvent event = new StockReservationEvent(
                workOrder.getId().toString(),
                itemsDto
        );

        sqsTemplate.send(to -> to.queue(stockQueueUrl).payload(event));
        System.out.println("Enviado comando de reserva para OS: " + workOrder.getId());
    }

    @Override
    public void publishStockCancellation(WorkOrder workOrder) {
        StockCancellationEvent event = new StockCancellationEvent(workOrder.getId());

        sqsTemplate.send(to -> to.queue(stockQueueUrl).payload(event));
        System.out.println("Enviado comando de cancelamento de reserva para OS: " + workOrder.getId());
    }

    @Override
    public void publishStockDecrease(WorkOrder workOrder) {
        StockDecreaseEvent event = new StockDecreaseEvent(workOrder.getId().toString());

        sqsTemplate.send(to -> to.queue(stockQueueUrl).payload(event));
        System.out.println("Enviado comando de baixa efetiva de estoque para OS: " + workOrder.getId());
    }

    @Override
    public void publishPaymentRequest(WorkOrder workOrder) {
        PaymentRequestEvent event = new PaymentRequestEvent(
                workOrder.getId().toString(),
                workOrder.getTotalAmount()
        );

        sqsTemplate.send(to -> to.queue(paymentQueueUrl).payload(event));
        System.out.println("Enviada solicitação de pagamento para OS: " + workOrder.getId());
    }
}