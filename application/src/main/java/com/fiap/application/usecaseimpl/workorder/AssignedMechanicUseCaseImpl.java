package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.user.UserGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.user.UserRole;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import com.fiap.usecase.workorder.AssignedMechanicUseCase;

import java.util.UUID;

public class AssignedMechanicUseCaseImpl implements AssignedMechanicUseCase {

    private final WorkOrderGateway workOrderGateway;
    private final UserGateway userGateway;

    public AssignedMechanicUseCaseImpl(WorkOrderGateway workOrderGateway, UserGateway userGateway) {
        this.workOrderGateway = workOrderGateway;
        this.userGateway = userGateway;
    }

    @Override
    public void execute(UUID workOrderId, UUID mechanicId) throws NotFoundException, BadRequestException {
        WorkOrder workOrder = workOrderGateway.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.WORK0001.getMessage(), ErrorCodeEnum.WORK0001.getCode()));

        User mechanic = userGateway.findById(mechanicId)
                .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.USE0007.getMessage(), ErrorCodeEnum.USE0007.getCode()));

        if (workOrder.getStatus() != WorkOrderStatus.RECEIVED) throw new BadRequestException(ErrorCodeEnum.WORK0003.getMessage(), ErrorCodeEnum.WORK0003.getCode());
        if (mechanic.getRole() != UserRole.MECHANIC) throw new BadRequestException(ErrorCodeEnum.USE0009.getMessage(), ErrorCodeEnum.USE0009.getCode());

        workOrder.setAssignedMechanic(mechanic);
        workOrder.setStatus(WorkOrderStatus.IN_DIAGNOSIS);

        workOrderGateway.save(workOrder);
    }
}
