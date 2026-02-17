package com.fiap.core.domain.workorder;

public enum WorkOrderStatus {
    RECEIVED("Recebido"),
    IN_DIAGNOSIS("Em diagnóstico"),
    APPROVAL_STOCK("Estoque aprovado"),
    REFUSED_STOCK("Estoque rejeitado"),
    AWAITING_APPROVAL("Aguardando aprovação"),
    REFUSED("Recusado"),
    IN_PROGRESS("Em andamento"),
    COMPLETED("Finalizado"),
    APPROVAL_PAYMENT("Pagamento aprovado"),
    REFUSED_PAYMENT("Pagamento rejeitado"),
    DELIVERED("Entregue");

    private final String description;

    WorkOrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static WorkOrderStatus fromString(String status) {
        if (status == null) {
            throw new IllegalArgumentException("Status não pode ser nulo.");
        }
        try {
            return WorkOrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }
}