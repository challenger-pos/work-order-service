package com.fiap.core.domain.workorder;

import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.part.Stock;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.core.exception.BadRequestException;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.core.exception.enums.ErrorCodeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class WorkOrder {
    private UUID id;
    private Customer customer;
    private Vehicle vehicle;
    private User createdBy;
    private User assignedMechanic;
    List<WorkOrderPart> workOrderParts;
    List<WorkOrderService> workOrderServices;
    private WorkOrderStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime finishedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime approvedAt;

    public WorkOrder(UUID customerId, UUID vehicleId, UUID createdById, List<WorkOrderPart> workOrderParts, List<WorkOrderService> workOrderServices) throws BadRequestException {

        if ((workOrderParts == null || workOrderParts.isEmpty()) && (workOrderServices == null || workOrderServices.isEmpty())) {
            throw new BadRequestException(ErrorCodeEnum.WORK0002.getMessage(), ErrorCodeEnum.WORK0002.getCode());
        }
        this.customer = new Customer(customerId);
        this.vehicle = new Vehicle(vehicleId);
        this.createdBy = new User(createdById);
        this.workOrderParts = workOrderParts;
        this.workOrderServices = workOrderServices;
        this.status = WorkOrderStatus.RECEIVED;
        // TODO [MS Estoque] Apos criar OS, status deve mudar para AWAITING_STOCK (nao RECEIVED direto).
        //   Publicar CMD_RESERVAR {workOrderId, itens[{partId, quantity}]} na fila q-estoque-cmd.
        //   Status so muda para RECEIVED quando EVT_RESERVADO for recebido via q-os-events.
        this.createdAt = LocalDateTime.now();
    }

    public WorkOrder(UUID id, Customer customer, Vehicle vehicle, User createdBy, List<WorkOrderPart> workOrderParts, List<WorkOrderService> workOrderServices, WorkOrderStatus status, BigDecimal totalAmount, LocalDateTime createdAt, LocalDateTime approvedAt, LocalDateTime finishedAt) {
        this.id = id;
        this.customer = customer;
        this.vehicle = vehicle;
        this.createdBy = createdBy;
        this.workOrderParts = workOrderParts;
        this.workOrderServices = workOrderServices;
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.approvedAt = approvedAt;
        this.finishedAt = finishedAt;
    }

    public WorkOrder(List<WorkOrderPart> workOrderParts, List<WorkOrderService> workOrderServices) throws BadRequestException {
        if ((workOrderParts == null || workOrderParts.isEmpty()) && (workOrderServices == null || workOrderServices.isEmpty())) {
            throw new BadRequestException(ErrorCodeEnum.WORK0002.getMessage(), ErrorCodeEnum.WORK0002.getCode());
        }

        this.workOrderParts = workOrderParts;
        this.workOrderServices = workOrderServices;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getAssignedMechanic() {
        return assignedMechanic;
    }

    public void setAssignedMechanic(User assignedMechanic) {
        this.assignedMechanic = assignedMechanic;
    }

    public List<WorkOrderPart> getWorkOrderParts() {
        return workOrderParts;
    }

    public void setWorkOrderParts(List<WorkOrderPart> workOrderParts) {
        this.workOrderParts = workOrderParts;
    }

    public List<WorkOrderService> getWorkOrderServices() {
        return workOrderServices;
    }

    public void setWorkOrderServices(List<WorkOrderService> workOrderServices) {
        this.workOrderServices = workOrderServices;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public void recalculateTotal() {
        BigDecimal totalParts = BigDecimal.ZERO;
        BigDecimal totalServices = BigDecimal.ZERO;

        if (workOrderParts != null && !workOrderParts.isEmpty()) {
            totalParts = workOrderParts.stream()
                    .map(part -> part.getAppliedPrice().multiply(BigDecimal.valueOf(part.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (workOrderServices != null && !workOrderServices.isEmpty()) {
            totalServices = workOrderServices.stream()
                    .map(service -> service.getAppliedPrice().multiply(BigDecimal.valueOf(service.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        this.totalAmount = totalParts.add(totalServices);
    }

    // TODO [MS Estoque] REMOVER este metodo - reserva de estoque sera feita pelo MS Estoque.
    //   Substituir por publish CMD_RESERVAR {workOrderId, itens[{partId, quantity}]} na fila q-estoque-cmd.
    //   A confirmacao chega via EVT_RESERVADO na fila q-os-events.
    public void reserveParts() throws BadRequestException, BusinessRuleException {
        for (WorkOrderPart part : workOrderParts) {
            if (part.getPart().getStock().getStockQuantity() < part.getQuantity()) {
                throw new BadRequestException(ErrorCodeEnum.PART0002.getMessage(), ErrorCodeEnum.PART0002.getCode());
            }
            part.getPart().getStock().subtract(part.getQuantity());
        }
    }

    // TODO [MS Estoque] REMOVER este metodo - estoque ja foi reservado pelo MS Estoque quando EVT_RESERVADO foi recebido.
    //   Na aprovacao, apenas mudar status para IN_PROGRESS (sem manipular estoque).
    public void approveStock() {
        Map<UUID, Integer> totalQuantityByPart = workOrderParts.stream()
                .collect(Collectors.toMap(
                        part -> part.getPart().getId(),
                        WorkOrderPart::getQuantity,
                        Integer::sum
                ));

        for (Map.Entry<UUID, Integer> entry : totalQuantityByPart.entrySet()) {
            UUID partId = entry.getKey();
            int totalQuantity = entry.getValue();

            List<WorkOrderPart> samePartsId = workOrderParts.stream()
                    .filter(p -> p.getPart().getId().equals(partId))
                    .toList();

            Stock stock = samePartsId.getFirst().getPart().getStock();

            stock.subtractReservedStock(totalQuantity);

            for (WorkOrderPart p : samePartsId) {
                p.getPart().setStock(stock);
            }
        }
    }

    // TODO [MS Estoque] SUBSTITUIR este metodo por publish CMD_CANCELAR_RESERVA {workOrderId} na fila q-estoque-cmd.
    //   O MS Estoque faz a restauracao do estoque reservado.
    public void restoreStock() throws BusinessRuleException {
        for (WorkOrderPart part : workOrderParts) {
            part.getPart().getStock().restore(part.getQuantity());
        }
    }
}
