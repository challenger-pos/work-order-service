package com.fiap.application.usecaseimpl.workorder;

import com.fiap.application.gateway.customer.CustomerGateway;
import com.fiap.application.gateway.part.PartGateway;
import com.fiap.application.gateway.service.ServiceGateway;
import com.fiap.application.gateway.user.UserGateway;
import com.fiap.application.gateway.vehicle.VehicleGateway;
import com.fiap.application.gateway.workorder.WorkOrderGateway;
import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.part.Money;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.service.Service;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderPart;
import com.fiap.core.domain.workorder.WorkOrderService;
import com.fiap.core.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateWorkOrderUseCaseImplTest {

    @Mock private WorkOrderGateway workOrderGateway;
    @Mock private CustomerGateway customerGateway;
    @Mock private VehicleGateway vehicleGateway;
    @Mock private UserGateway userGateway;
    @Mock private PartGateway partGateway;
    @Mock private ServiceGateway serviceGateway;

    private CreateWorkOrderUseCaseImpl useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateWorkOrderUseCaseImpl(
                workOrderGateway, customerGateway, vehicleGateway, userGateway, partGateway, serviceGateway
        );
    }

    @Test
    void shouldPopulateItemsSaveAndReturn() throws Exception {
        UUID custId = UUID.randomUUID();
        UUID vehId = UUID.randomUUID();
        UUID usrId = UUID.randomUUID();
        UUID partId = UUID.randomUUID();
        UUID serviceId = UUID.randomUUID();
        UUID savedWorkOrderId = UUID.randomUUID();

        WorkOrder inputWorkOrder = mock(WorkOrder.class);

        when(inputWorkOrder.getCustomer()).thenReturn(new Customer(custId));
        when(inputWorkOrder.getVehicle()).thenReturn(new Vehicle(vehId));
        when(inputWorkOrder.getCreatedBy()).thenReturn(new User(usrId));

        Customer customer = mock(Customer.class);
        Vehicle vehicle = mock(Vehicle.class);
        User user = mock(User.class);

        when(customerGateway.findById(custId)).thenReturn(Optional.of(customer));
        when(vehicleGateway.findById(vehId)).thenReturn(Optional.of(vehicle));
        when(userGateway.findById(usrId)).thenReturn(Optional.of(user));

        WorkOrderPart inPart = mock(WorkOrderPart.class);
        when(inPart.getPartId()).thenReturn(partId);
        when(inPart.getQuantity()).thenReturn(2);

        WorkOrderService inService = mock(WorkOrderService.class);
        when(inService.getServiceId()).thenReturn(serviceId);
        when(inService.getQuantity()).thenReturn(1);

        when(inputWorkOrder.getWorkOrderParts()).thenReturn(List.of(inPart));
        when(inputWorkOrder.getWorkOrderServices()).thenReturn(List.of(inService));

        Part part = mock(Part.class);
        when(part.getId()).thenReturn(partId);
        when(part.getPrice()).thenReturn(Money.of(new BigDecimal("100.00")));
        when(partGateway.findByIds(List.of(partId))).thenReturn(List.of(part));

        Service service = mock(Service.class);
        when(service.getId()).thenReturn(serviceId);
        when(service.getBasePrice()).thenReturn(new BigDecimal("200.00"));
        when(serviceGateway.findByIds(List.of(serviceId))).thenReturn(List.of(service));

        WorkOrder savedOrder = mock(WorkOrder.class);
        when(savedOrder.getId()).thenReturn(savedWorkOrderId);
        when(savedOrder.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(workOrderGateway.save(inputWorkOrder)).thenReturn(savedOrder);

        WorkOrder result = useCase.execute(inputWorkOrder);

        assertSame(savedOrder, result);

        verify(inputWorkOrder).setCustomer(customer);
        verify(inputWorkOrder).setVehicle(vehicle);
        verify(inputWorkOrder).setCreatedBy(user);
        verify(inputWorkOrder).setWorkOrderParts(anyList());
        verify(inputWorkOrder).setWorkOrderServices(anyList());
        verify(inputWorkOrder).recalculateTotal();

        verify(workOrderGateway).save(inputWorkOrder);

        ArgumentCaptor<WorkOrderHistory> historyCaptor = ArgumentCaptor.forClass(WorkOrderHistory.class);
        verify(workOrderGateway).saveHistory(historyCaptor.capture());

        WorkOrderHistory capturedHistory = historyCaptor.getValue();
        assertEquals(savedWorkOrderId, capturedHistory.getWorkOrderId());
        assertEquals("RECEIVED", capturedHistory.getStatus().name());
    }

    @Test
    void shouldStopWhenCustomerNotFound() {
        UUID custId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);
        when(workOrder.getCustomer()).thenReturn(new Customer(custId));
        when(customerGateway.findById(custId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(workOrder));

        verify(customerGateway).findById(custId);
        verifyNoInteractions(vehicleGateway, userGateway, partGateway, serviceGateway, workOrderGateway);
    }

    @Test
    void shouldStopWhenVehicleNotFound() {
        UUID custId = UUID.randomUUID();
        UUID vehId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);

        when(workOrder.getCustomer()).thenReturn(new Customer(custId));
        when(workOrder.getVehicle()).thenReturn(new Vehicle(vehId));

        when(customerGateway.findById(custId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleGateway.findById(vehId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(workOrder));

        verify(customerGateway).findById(custId);
        verify(vehicleGateway).findById(vehId);
        verifyNoInteractions(userGateway, partGateway, serviceGateway, workOrderGateway);
    }

    @Test
    void shouldStopWhenUserNotFound() throws NotFoundException {
        UUID custId = UUID.randomUUID();
        UUID vehId = UUID.randomUUID();
        UUID usrId = UUID.randomUUID();
        WorkOrder workOrder = mock(WorkOrder.class);

        when(workOrder.getCustomer()).thenReturn(new Customer(custId));
        when(workOrder.getVehicle()).thenReturn(new Vehicle(vehId));
        when(workOrder.getCreatedBy()).thenReturn(new User(usrId));

        when(customerGateway.findById(custId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleGateway.findById(vehId)).thenReturn(Optional.of(mock(Vehicle.class)));
        when(userGateway.findById(usrId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> useCase.execute(workOrder));

        verify(customerGateway).findById(custId);
        verify(vehicleGateway).findById(vehId);
        verify(userGateway).findById(usrId);
        verifyNoInteractions(partGateway, serviceGateway, workOrderGateway);
    }

    @Test
    void shouldCreateWorkOrderWithoutPartsAndServicesSuccessfully() throws Exception {
        UUID custId = UUID.randomUUID();
        UUID vehId = UUID.randomUUID();
        UUID usrId = UUID.randomUUID();
        UUID savedWorkOrderId = UUID.randomUUID();

        WorkOrder inputWorkOrder = mock(WorkOrder.class);

        when(inputWorkOrder.getCustomer()).thenReturn(new Customer(custId));
        when(inputWorkOrder.getVehicle()).thenReturn(new Vehicle(vehId));
        when(inputWorkOrder.getCreatedBy()).thenReturn(new User(usrId));

        when(customerGateway.findById(custId)).thenReturn(Optional.of(mock(Customer.class)));
        when(vehicleGateway.findById(vehId)).thenReturn(Optional.of(mock(Vehicle.class)));
        when(userGateway.findById(usrId)).thenReturn(Optional.of(mock(User.class)));

        when(inputWorkOrder.getWorkOrderParts()).thenReturn(List.of());
        when(inputWorkOrder.getWorkOrderServices()).thenReturn(List.of());

        when(partGateway.findByIds(List.of())).thenReturn(List.of());
        when(serviceGateway.findByIds(List.of())).thenReturn(List.of());

        WorkOrder savedOrder = mock(WorkOrder.class);
        when(savedOrder.getId()).thenReturn(savedWorkOrderId);
        when(savedOrder.getCreatedAt()).thenReturn(LocalDateTime.now());
        when(workOrderGateway.save(inputWorkOrder)).thenReturn(savedOrder);

        WorkOrder result = useCase.execute(inputWorkOrder);

        assertSame(savedOrder, result);

        verify(inputWorkOrder).setWorkOrderParts(List.of());
        verify(inputWorkOrder).setWorkOrderServices(List.of());
        verify(inputWorkOrder).recalculateTotal();
        verify(workOrderGateway).save(inputWorkOrder);
        verify(workOrderGateway).saveHistory(any());
    }
}