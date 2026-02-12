package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.service.Service;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.core.domain.workorder.WorkOrderService;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import com.fiap.usecase.workorder.AddItemsWorkOrderUseCase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AddItemsWorkOrderUseCaseImpl implements AddItemsWorkOrderUseCase {

    private final WorkOrderGateway workOrderGateway;

    public AddItemsWorkOrderUseCaseImpl(WorkOrderGateway workOrderGateway, PartGateway partGateway, ServiceGateway serviceGateway) {
        this.workOrderGateway = workOrderGateway;
        this.partGateway = partGateway;
        this.serviceGateway = serviceGateway;
    }

    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;

    @Override
    public WorkOrder execute(UUID workOrderId, WorkOrder increaseWorkOrder) throws NotFoundException, BusinessRuleException, BadRequestException {

        WorkOrder workOrder = workOrderGateway.findById(workOrderId)
                .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.WORK0001.getMessage(), ErrorCodeEnum.WORK0001.getCode()));

        List<UUID> partIds = increaseWorkOrder.getWorkOrderParts().stream()
                .map(WorkOrderPart::getPartId)
                .toList();

        List<UUID> servicesIds = increaseWorkOrder.getWorkOrderServices().stream()
                .map(WorkOrderService::getServiceId)
                .toList();

        List<Part> parts = partGateway.findByIds(partIds);
        List<Service> services = serviceGateway.findByIds(servicesIds);

        Map<UUID, WorkOrderPart> workOrderPartMap = increaseWorkOrder.getWorkOrderParts().stream()
                .collect(Collectors.toMap(WorkOrderPart::getPartId, p -> p));

        Map<UUID, WorkOrderService> workOrderServiceMap = increaseWorkOrder.getWorkOrderServices().stream()
                .collect(Collectors.toMap(WorkOrderService::getServiceId, s -> s));

        List<WorkOrderPart> workOrderParts = populateParts(parts,workOrderPartMap, increaseWorkOrder);
        List<WorkOrderService> workOrderServices = populateServices(services, workOrderServiceMap, increaseWorkOrder);

        workOrder.getWorkOrderServices().addAll(workOrderServices);
        workOrder.getWorkOrderParts().addAll(workOrderParts);

        workOrder.recalculateTotal();

        //TODO Chamar fila para validar Estoque
        workOrder.setStatus(WorkOrderStatus.APPROVAL_STOCK);

        return workOrderGateway.save(workOrder);
    }

    private List<WorkOrderPart> populateParts(List<Part> parts, Map<UUID, WorkOrderPart> workOrderPartMap, WorkOrder workOrder) {
        return parts.stream()
                .map(p -> {
                    WorkOrderPart workOrderPart = workOrderPartMap.get(p.getId());
                    return new WorkOrderPart(
                            p.getId(),
                            workOrderPart.getQuantity(),
                            p,
                            p.getPrice(),
                            workOrder

                    );
                })
                .toList();
    }

    private List<WorkOrderService> populateServices(List<Service> services, Map<UUID, WorkOrderService> workOrderServiceMap, WorkOrder workOrder) {
        return services.stream()
                .map(s -> {
                    WorkOrderService workOrderService = workOrderServiceMap.get(s.getId());
                    return new WorkOrderService(
                            s.getId(),
                            workOrderService.getQuantity(),
                            s,
                            s.getBasePrice(),
                            workOrder

                    );
                })
                .toList();
    }
}
