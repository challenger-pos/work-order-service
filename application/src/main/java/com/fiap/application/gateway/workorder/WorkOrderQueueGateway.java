package com.fiap.application.gateway.workorder;

import com.fiap.core.domain.workorder.WorkOrder;

public interface WorkOrderQueueGateway {

    void publishStockReservation(WorkOrder workOrder);

    void publishStockCancellation(WorkOrder workOrder);

    void publishStockDecrease(WorkOrder workOrder);

    void publishPaymentRequest(WorkOrder workOrder);
}