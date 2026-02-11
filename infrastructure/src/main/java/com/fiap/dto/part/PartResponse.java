package com.fiap.dto.part;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PartResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        // TODO [MS Estoque] REMOVER stockQuantity, reservedStock e minimumStock - dados de estoque nao existem neste MS
        Integer stockQuantity,
        Integer reservedStock,
        Integer minimumStock,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}