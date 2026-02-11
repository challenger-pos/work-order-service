package com.fiap.core.domain.part;

import com.fiap.core.exception.BusinessRuleException;
import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Part {

	private UUID id;
	private String name;
	private String description;
	private Money price;
	// TODO [MS Estoque] REMOVER campo stock - estoque sera gerenciado pelo MS Estoque.
	//   Part mantem apenas dados cadastrais (nome, descricao, preco).
	private Stock stock;
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;

	// TODO [MS Estoque] REMOVER subtractFromStock() - estoque gerenciado pelo MS Estoque via CMD_RESERVAR na q-estoque-cmd
	public void subtractFromStock(int quantity) throws BusinessRuleException {
		this.stock.subtract(quantity);
	}

	// TODO [MS Estoque] REMOVER returnToStock() - estoque gerenciado pelo MS Estoque via CMD_CANCELAR_RESERVA na q-estoque-cmd
	public void returnToStock(int quantity) throws BusinessRuleException {
		this.stock.restore(quantity);
	}
}