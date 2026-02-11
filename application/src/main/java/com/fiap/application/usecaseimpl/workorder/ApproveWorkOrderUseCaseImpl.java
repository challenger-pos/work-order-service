package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.ForbiddenException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.UnauthorizedException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import com.fiap.usecase.workorder.ApproveWorkOrderUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ApproveWorkOrderUseCaseImpl implements ApproveWorkOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ApproveWorkOrderUseCaseImpl.class);
    private final WorkOrderGateway workOrderGateway;
    private final PartGateway partGateway;

    public ApproveWorkOrderUseCaseImpl(WorkOrderGateway workOrderGateway, PartGateway partGateway) {
        this.workOrderGateway = workOrderGateway;
        this.partGateway = partGateway;
    }

    @Override
    public void execute(UUID id, String documentNumber) throws NotFoundException, BadRequestException, UnauthorizedException, ForbiddenException {
        try {
            MDC.put("workorder.id", id.toString());

            if (documentNumber == null) {
                throw  new UnauthorizedException(ErrorCodeEnum.WORK0008.getMessage(), ErrorCodeEnum.WORK0008.getCode());
            }

            logger.debug("Executing approve work order use case for work order: {}", id);
            WorkOrder workOrder = workOrderGateway.findById(id)
                    .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.WORK0001.getMessage(), ErrorCodeEnum.WORK0001.getCode()));

            if (!WorkOrderStatus.AWAITING_APPROVAL.equals(workOrder.getStatus())) {
                logger.warn("Cannot approve work order: {} - Current status: {} - Expected: AWAITING_APPROVAL",
                        id, workOrder.getStatus());
                throw new BadRequestException(
                  ErrorCodeEnum.WORK0006.getMessage(), ErrorCodeEnum.WORK0006.getCode());
            }

            if (!Objects.equals(workOrder.getCustomer().getDocumentNumber().getValue(), documentNumber)) {
                throw new ForbiddenException(ErrorCodeEnum.WORK0007.getMessage(), ErrorCodeEnum.WORK0007.getCode());
            }
            MDC.put("workorder.status", workOrder.getStatus().getDescription());
            logger.info("Approving work order: {} - Parts count: {}", id, workOrder.getWorkOrderParts().size());

            // TODO [MS Estoque] REMOVER workOrder.approveStock() - estoque ja foi reservado pelo MS Estoque.
            //   Approve apenas muda status para IN_PROGRESS (sem manipular estoque).
            workOrder.approveStock();
            workOrder.setStatus(WorkOrderStatus.IN_PROGRESS);
            workOrder.setApprovedAt(LocalDateTime.now());

            // TODO [MS Estoque] REMOVER bloco abaixo (extrai parts + partGateway.saveAll) - nao manipula mais estoque diretamente
            List<Part> parts = workOrder.getWorkOrderParts().stream()
                    .map(WorkOrderPart::getPart)
                    .toList();

            partGateway.saveAll(parts);
            workOrderGateway.save(workOrder);

            // Salvar histórico com status IN_PROGRESS
            WorkOrderHistory history = new WorkOrderHistory(workOrder.getId(), WorkOrderStatus.IN_PROGRESS);
            history.setCreatedAt(LocalDateTime.now());
            workOrderGateway.saveHistory(history);

            logger.info("Work order approved successfully: {} - New status: IN_PROGRESS", id);
        } finally {
            MDC.remove("workorder.id");
            MDC.remove("workorder.status");
        }
    }
}
