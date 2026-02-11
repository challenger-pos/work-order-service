package com.fiap.core.domain.part;

import com.fiap.core.exception.BusinessRuleException;
import lombok.Getter;

// TODO [MS Estoque] Esta classe inteira migra para o MS Estoque - sera REMOVIDA deste microservico.
//   Todo gerenciamento de estoque (reserva, baixa, restauracao) sera feito pelo MS Estoque via filas.
//   Filas: q-estoque-cmd (comandos) e q-os-events (eventos de resposta).
@Getter
public class Stock {

    private int stockQuantity;
    private int reservedStock;
    private final int minimumStock;

    private Stock(int stockQuantity, int reservedStock, int minimumStock) {
        this.stockQuantity = stockQuantity;
        this.reservedStock = reservedStock;
        this.minimumStock = minimumStock;
    }

    public static Stock of(int stockQuantity, int reservedStock, int minimumStock) throws BusinessRuleException {
        if (stockQuantity < 0 || reservedStock < 0 || minimumStock < 0) {
            throw new BusinessRuleException("Stock values cannot be negative.", "STOCK-001");
        }
        return new Stock(stockQuantity, reservedStock, minimumStock);
    }

    public void subtract(int quantity) throws BusinessRuleException {
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantidade de peças deve ser um número positivo.", "STOCK-002");
        }
        if (this.stockQuantity < quantity) {
            throw new BusinessRuleException("Estoque insuficiente.", "STOCK-003");
        }
        this.stockQuantity -= quantity;
        this.reservedStock += quantity;
    }

    public void restore(int quantity) throws BusinessRuleException {
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity to restore must be positive.", "STOCK-004");
        }
        if (this.reservedStock < quantity) {
            throw new BusinessRuleException("Cannot restore more than what is reserved.", "STOCK-005");
        }
        this.stockQuantity += quantity;
        this.reservedStock -= quantity;
    }

    public void subtractReservedStock(int quantity) {
        this.reservedStock -= quantity;
    }
}