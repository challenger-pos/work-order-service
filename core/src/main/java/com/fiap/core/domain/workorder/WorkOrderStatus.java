package com.fiap.core.domain.workorder;

public enum WorkOrderStatus {
    RECEIVED("Recebido"),
    // TODO [MS Estoque] Adicionar novos status:
    //   AWAITING_STOCK("Aguardando confirmação de estoque") - OS aguarda resposta do MS Estoque via fila q-os-events
    //   REJECTED_STOCK("Estoque indisponível") - OS finalizada quando EVT_FALHA_RESERVA recebido via q-os-events
    IN_DIAGNOSIS("Em diagnóstico"),
    AWAITING_APPROVAL("Aguardando aprovação"),
    REFUSED("Recusado"),
    IN_PROGRESS("Em andamento"),
    COMPLETED("Finalizado"),
    // TODO [MS Pagamento] Adicionar novos status:
    //   AWAITING_PAYMENT("Aguardando pagamento") - Apos COMPLETED, aguarda confirmacao de pagamento via q-pgto-events
    //   REFUSED_PAYMENT("Pagamento recusado") - Quando EVT_PAGAMENTO_FALHOU recebido, publica CMD_REPOR_ESTOQUE na q-estoque-cmd
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