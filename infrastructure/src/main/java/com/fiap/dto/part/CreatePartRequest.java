package com.fiap.dto.part;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CreatePartRequest(
        @NotBlank String name,
        String description,
        @NotNull @Min(0) BigDecimal price,
        // TODO [MS Estoque] REMOVER stockQuantity e minimumStock - estoque gerenciado pelo MS Estoque
        @NotNull @Min(0) Integer stockQuantity,
        @NotNull @Min(0) Integer minimumStock
) {}