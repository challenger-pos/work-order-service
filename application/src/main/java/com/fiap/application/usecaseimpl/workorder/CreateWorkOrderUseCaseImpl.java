package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.customer.CustomerGateway;
import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.user.UserGateway;
import com.fiap.application.gateway.vehicle.VehicleGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.service.Service;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.core.domain.workorder.WorkOrderService;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import com.fiap.usecase.workorder.CreateWorkOrderUseCase;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class CreateWorkOrderUseCaseImpl implements CreateWorkOrderUseCase {

    private final WorkOrderGateway workOrderGateway;
    private final CustomerGateway customerGateway;
    private final VehicleGateway vehicleGateway;
    private final UserGateway userGateway;
    private final PartGateway partGateway;
    private final ServiceGateway serviceGateway;

    public CreateWorkOrderUseCaseImpl(WorkOrderGateway workOrderGateway, CustomerGateway customerGateway, VehicleGateway vehicleGateway, UserGateway userGateway, PartGateway partGateway, ServiceGateway serviceGateway) {
        this.workOrderGateway = workOrderGateway;
        this.customerGateway = customerGateway;
        this.vehicleGateway = vehicleGateway;
        this.userGateway = userGateway;
        this.partGateway = partGateway;
        this.serviceGateway = serviceGateway;
    }

    @Override
    public WorkOrder execute(WorkOrder workOrder) throws NotFoundException, BusinessRuleException, BadRequestException {
        Customer customer = customerGateway.findById(workOrder.getCustomer().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.CUST0001.getMessage(), ErrorCodeEnum.CUST0001.getCode()));

        Vehicle vehicle = vehicleGateway.findById(workOrder.getVehicle().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.VEH0001.getMessage(), ErrorCodeEnum.VEH0001.getCode()));

        User createdBy = userGateway.findById(workOrder.getCreatedBy().getId())
                .orElseThrow(() -> new NotFoundException(ErrorCodeEnum.USE0007.getMessage(), ErrorCodeEnum.USE0007.getCode()));

        List<UUID> partIds = workOrder.getWorkOrderParts().stream()
                .map(WorkOrderPart::getPartId)
                .toList();

        List<UUID> servicesIds = workOrder.getWorkOrderServices().stream()
                .map(WorkOrderService::getServiceId)
                .toList();

        List<Part> parts = partGateway.findByIds(partIds);
        List<Service> services = serviceGateway.findByIds(servicesIds);

        Map<UUID, WorkOrderPart> workOrderPartMap = workOrder.getWorkOrderParts().stream()
                        .collect(Collectors.toMap(WorkOrderPart::getPartId, p -> p));

        Map<UUID, WorkOrderService> workOrderServiceMap = workOrder.getWorkOrderServices().stream()
                .collect(Collectors.toMap(WorkOrderService::getServiceId, s -> s));

        List<WorkOrderPart> workOrderParts = populateParts(parts,workOrderPartMap, workOrder);
        List<WorkOrderService> workOrderServices = populateServices(services, workOrderServiceMap, workOrder);
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setCreatedBy(createdBy);
        workOrder.setWorkOrderParts(workOrderParts);
        workOrder.setWorkOrderServices(workOrderServices);

        workOrder.recalculateTotal();
        WorkOrder savedWorkOrder = workOrderGateway.save(workOrder);

        WorkOrderHistory history = new WorkOrderHistory(savedWorkOrder.getId(), WorkOrderStatus.RECEIVED);

        history.setCreatedAt(savedWorkOrder.getCreatedAt());
        workOrderGateway.saveHistory(history);

        return savedWorkOrder;
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
