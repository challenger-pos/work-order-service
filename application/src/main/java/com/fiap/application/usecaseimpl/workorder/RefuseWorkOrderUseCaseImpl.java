package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.*;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import com.fiap.usecase.workorder.RefuseWorkOrderUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
public class RefuseWorkOrderUseCaseImpl implements RefuseWorkOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RefuseWorkOrderUseCaseImpl.class);
    private final WorkOrderGateway workOrderGateway;
    private final WorkOrderQueueGateway workOrderQueueGateway;
    private final PartGateway partGateway;

    @Override
    public void execute(UUID id, String documentNumber) throws NotFoundException, BadRequestException, BusinessRuleException, UnauthorizedException, ForbiddenException {
        try {
            MDC.put("workorder.id", id.toString());

            if (documentNumber == null) {
                throw  new UnauthorizedException(ErrorCodeEnum.WORK0008.getMessage(), ErrorCodeEnum.WORK0008.getCode());
            }

            logger.debug("Executing refuse work order use case for work order: {}", id);

            WorkOrder workOrder = workOrderGateway.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.WORK0001.getMessage(), ErrorCodeEnum.WORK0001.getCode()));

            if (!WorkOrderStatus.AWAITING_APPROVAL.equals(workOrder.getStatus()))
                throw new BadRequestException(ErrorCodeEnum.WORK0006.getMessage(), ErrorCodeEnum.WORK0006.getCode());

            if (!Objects.equals(workOrder.getCustomer().getDocumentNumber().getValue(), documentNumber)) {
                throw new ForbiddenException(ErrorCodeEnum.WORK0007.getMessage(), ErrorCodeEnum.WORK0007.getCode());
            }

            MDC.put("workorder.status", workOrder.getStatus().getDescription());

            if (!WorkOrderStatus.AWAITING_APPROVAL.equals(workOrder.getStatus())) {
                logger.warn("Cannot refuse work order: {} - Current status: {} - Expected: AWAITING_APPROVAL",
                        id, workOrder.getStatus());
                throw new BadRequestException(ErrorCodeEnum.WORK0006.getMessage(), ErrorCodeEnum.WORK0006.getCode());
            }

            logger.info("Refusing work order: {} - Restoring stock for {} parts", id, workOrder.getWorkOrderParts().size());

            workOrder.setStatus(WorkOrderStatus.REFUSED);
            workOrder.setFinishedAt(LocalDateTime.now());

            workOrderGateway.save(workOrder);

            workOrderQueueGateway.publishStockCancellation(workOrder);

            // Salvar histórico com status REFUSED
            WorkOrderHistory history = new WorkOrderHistory(workOrder.getId(), WorkOrderStatus.REFUSED);
            history.setCreatedAt(LocalDateTime.now());
            workOrderGateway.saveHistory(history);

            logger.info("Work order refused successfully: {} - Stock restored - New status: COMPLETED", id);
        } finally {
            MDC.remove("workorder.id");
            MDC.remove("workorder.status");
        }
    }
}
