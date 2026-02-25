package com.fiap.core.domain.workorder;

import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
public class WorkOrderHistory {
    private UUID workOrderId;
    private WorkOrder workOrder;
    private WorkOrderStatus status;
    private String notes;
    private LocalDateTime createdAt;

    public WorkOrderHistory(UUID workOrderId, WorkOrderStatus status) {
        this.workOrderId = workOrderId;
        this.status = status;
    }

    public WorkOrderHistory(UUID workOrderId, WorkOrder workOrder, WorkOrderStatus status, String notes, LocalDateTime createdAt) {
        this.workOrderId = workOrderId;
        this.workOrder = workOrder;
        this.status = status;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public UUID getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(UUID workOrderId) {
        this.workOrderId = workOrderId;
    }

    public WorkOrder getWorkOrder() {
        return workOrder;
    }

    public void setWorkOrder(WorkOrder workOrder) {
        this.workOrder = workOrder;
    }

    public WorkOrderStatus getStatus() {
        return status;
    }

    public void setStatus(WorkOrderStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
