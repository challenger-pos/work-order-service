package com.fiap.config;

import com.fiap.application.gateway.customer.CustomerGateway;
import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.user.UserGateway;
import com.fiap.application.gateway.vehicle.VehicleGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.application.gateway.workorder.WorkOrderQueueGateway;
import com.fiap.application.usecaseimpl.workorder.*;
import com.fiap.usecase.workorder.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WorkOrderConfig {

    @Bean
    public CreateWorkOrderUseCase createWorkOrderUseCase(WorkOrderGateway workOrderGateway,CustomerGateway customerGateway, VehicleGateway vehicleGateway, UserGateway userGateway, PartGateway partGateway, ServiceGateway serviceGateway) {
        return new CreateWorkOrderUseCaseImpl(workOrderGateway, customerGateway, vehicleGateway, userGateway, partGateway, serviceGateway);
    }

    @Bean
    public FindWorkOrderByIdUseCase findWorkOrderByIdUseCase(WorkOrderGateway workOrderGateway) {
        return new FindWorkOrderByIdUseCaseImpl(workOrderGateway);
    }

    @Bean
    public AssignedMechanicUseCase assignedMechanicUseCase(WorkOrderGateway workOrderGateway, UserGateway userGateway) {
        return new AssignedMechanicUseCaseImpl(workOrderGateway, userGateway);
    }

    @Bean
    public UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase(WorkOrderGateway workOrderGateway, ServiceGateway serviceGateway, WorkOrderQueueGateway workOrderQueueGateway) { // Injeção da dependência
        return new UpdateStatusWorkOrderUseCaseImpl(workOrderGateway, serviceGateway, workOrderQueueGateway);
    }

    @Bean
    public GetWorkOrderStatusUseCase getWorkOrderStatusUseCase(WorkOrderGateway workOrderGateway) {
        return new GetWorkOrderStatusUseCaseImpl(workOrderGateway);
    }

    @Bean
    public ApproveWorkOrderUseCase approveWorkOrderUseCase(WorkOrderGateway workOrderGateway, WorkOrderQueueGateway workOrderQueueGateway, PartGateway partGateway) {
        return new ApproveWorkOrderUseCaseImpl(workOrderGateway, workOrderQueueGateway, partGateway);
    }

    @Bean
    public RefuseWorkOrderUseCase refuseWorkOrderUseCase(WorkOrderGateway workOrderGateway, WorkOrderQueueGateway workOrderQueueGateway, PartGateway partGateway) {
        return new RefuseWorkOrderUseCaseImpl(workOrderGateway, workOrderQueueGateway, partGateway);
    }

    @Bean
    public AddItemsWorkOrderUseCase addItemsWorkOrderUseCase(WorkOrderGateway workOrderGateway, PartGateway partGateway, ServiceGateway serviceGateway) {
        return new AddItemsWorkOrderUseCaseImpl(workOrderGateway,partGateway,serviceGateway);
    }

    @Bean
    public ListWorkOrdersByStatusUseCase listWorkOrdersByStatusUseCase(WorkOrderGateway workOrderGateway) {
        return new ListWorkOrdersByStatusUseCaseImpl(workOrderGateway);
    }

    @Bean
    public GetWorkOrderHistoryUseCase getWorkOrderHistoryUseCase(WorkOrderGateway workOrderGateway) {
        return new GetWorkOrderHistoryUseCaseImpl(workOrderGateway);
    }

}
