package com.fiap.infrastructure.messaging.listener;

import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.gateway.messaging.dto.StockEventDto;
import com.fiap.usecase.workorder.UpdateStatusWorkOrderUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.springframework.stereotype.Component;

@Component
public class WorkOrderSqsListener {

    private final UpdateStatusWorkOrderUseCase updateStatusUseCase;

    public WorkOrderSqsListener(UpdateStatusWorkOrderUseCase updateStatusUseCase) {
        this.updateStatusUseCase = updateStatusUseCase;
    }

    @SqsListener("${aws.sqs.queue.os-events}")
    public void receiveStockEvent(StockEventDto event) throws NotFoundException, BadRequestException {
        System.out.println("Evento recebido do Estoque: " + event.getType());

        if ("RESERVA_CONFIRMADA".equals(event.getType())) {
            updateStatusUseCase.execute(event.getWorkOrderId(), WorkOrderStatus.AWAITING_APPROVAL.toString());
        }
        else if ("FALHA_RESERVA".equals(event.getType())) {
            updateStatusUseCase.execute(event.getWorkOrderId(), WorkOrderStatus.REFUSED_STOCK.toString());
        }
    }
}