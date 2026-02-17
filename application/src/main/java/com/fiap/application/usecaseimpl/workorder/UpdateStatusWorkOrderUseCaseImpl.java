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
import com.fiap.usecase.workorder.UpdateStatusWorkOrderUseCase;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UpdateStatusWorkOrderUseCaseImpl implements UpdateStatusWorkOrderUseCase {

    private final WorkOrderGateway workOrderGateway;
    private final ServiceGateway serviceGateway;
    private final WorkOrderQueueGateway workOrderQueueGateway;

    @Override
    public WorkOrder execute(UUID id, String newStatusStr)
            throws NotFoundException, BadRequestException {

        WorkOrder workOrder = workOrderGateway.findById(id)
                .orElseThrow(() -> new NotFoundException(
                        ErrorCodeEnum.WORK0001.getMessage(),
                        ErrorCodeEnum.WORK0001.getCode()
                ));

        final WorkOrderStatus newStatus;
        try {
            newStatus = WorkOrderStatus.fromString(newStatusStr);
        } catch (Exception ex) {
            throw new BadRequestException(
                    ErrorCodeEnum.WORK0004.getMessage(),
                    ErrorCodeEnum.WORK0004.getCode()
            );
        }

        if (workOrder.getStatus() == newStatus) {
            throw new BadRequestException(ErrorCodeEnum.WORK0005.getMessage(), ErrorCodeEnum.WORK0005.getCode());
        }

        if (newStatus == WorkOrderStatus.IN_PROGRESS) {
            workOrderQueueGateway.publishStockDecrease(workOrder);
        }

        if (newStatus == WorkOrderStatus.COMPLETED) {
            workOrder.setFinishedAt(LocalDateTime.now());
            workOrderQueueGateway.publishPaymentRequest(workOrder);
        }

        if (newStatus == WorkOrderStatus.DELIVERED) {
            workOrder.setFinishedAt(LocalDateTime.now());
        }

        workOrder.setStatus(newStatus);
        workOrder.setUpdatedAt(LocalDateTime.now());

        WorkOrder updatedWorkOrder = workOrderGateway.update(workOrder);

        saveHistoryAndMetrics(updatedWorkOrder, id);

        return updatedWorkOrder;
    }

    private void saveHistoryAndMetrics(WorkOrder workOrder, UUID id) {
        WorkOrderHistory history = new WorkOrderHistory(workOrder.getId(), workOrder.getStatus());
        history.setCreatedAt(LocalDateTime.now());
        workOrderGateway.saveHistory(history);

        try {
            List<WorkOrderHistory> historyList = workOrderGateway.findHistoryByWorkOrderIdOrderByCreatedAtDesc(id);
            if (historyList.size() >= 2) {
                WorkOrderHistory latest = historyList.get(0);
                WorkOrderHistory previous = historyList.get(1);

                if (latest.getCreatedAt() != null && previous.getCreatedAt() != null) {
                    long durationInSeconds = Duration.between(previous.getCreatedAt(), latest.getCreatedAt()).getSeconds();
                    String statusTag = "status:" + latest.getStatus().name();
                    serviceGateway.sendWorkOrderStatusTransitionDuration(durationInSeconds, statusTag);
                }
            }
        } catch (Exception e) {
        }
    }
}