package com.fiap.mapper.part;

import com.fiap.core.domain.part.Money;
import com.fiap.core.domain.part.Part;
import com.fiap.core.domain.part.Stock;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.dto.part.CreatePartRequest;
import com.fiap.dto.part.PartResponse;
import com.fiap.dto.part.UpdatePartRequest;
import com.fiap.persistence.entity.part.PartEntity;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class PartMapper {

    public Part toDomain(CreatePartRequest request) throws BusinessRuleException {
        return Part.builder()
                .name(request.name())
                .description(request.description())
                .price(Money.of(request.price()))
                // TODO [MS Estoque] REMOVER mapeamento de Stock - estoque gerenciado pelo MS Estoque
                .stock(Stock.of(request.stockQuantity(), 0, request.minimumStock()))
                .build();
    }

    public Part toDomain(UUID id, UpdatePartRequest request) throws BusinessRuleException {
        return Part.builder()
                .id(id)
                .name(request.name())
                .description(request.description())
                .price(Money.of(request.price()))
                // TODO [MS Estoque] REMOVER mapeamento de Stock - estoque gerenciado pelo MS Estoque
                .stock(Stock.of(request.stockQuantity(), request.reservedStock(), request.minimumStock()))
                .build();
    }

    public PartResponse toResponse(Part part) {
        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getDescription(),
                part.getPrice().getValue(),
                // TODO [MS Estoque] REMOVER campos de stock no toResponse - estoque nao existe neste MS
                part.getStock().getStockQuantity(),
                part.getStock().getReservedStock(),
                part.getStock().getMinimumStock(),
                part.getCreatedAt(),
                part.getUpdatedAt()
        );
    }

    public PartEntity toEntity(Part part) {
        return PartEntity.builder()
                .id(part.getId())
                .name(part.getName())
                .description(part.getDescription())
                .price(part.getPrice().getValue())
                // TODO [MS Estoque] REMOVER campos de stock no toEntity - estoque nao existe neste MS
                .stockQuantity(part.getStock().getStockQuantity())
                .reservedStock(part.getStock().getReservedStock())
                .minimumStock(part.getStock().getMinimumStock())
                .createdAt(part.getCreatedAt())
                .updatedAt(part.getUpdatedAt())
                .build();
    }

    public Part toDomain(PartEntity entity) {
        try {
            return Part.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .description(entity.getDescription())
                    .price(Money.of(entity.getPrice()))
                    // TODO [MS Estoque] REMOVER reconstrucao de Stock no toDomain(PartEntity) - estoque gerenciado pelo MS Estoque
                    .stock(Stock.of(entity.getStockQuantity(), entity.getReservedStock(), entity.getMinimumStock()))
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        } catch (BusinessRuleException e) {
            throw new IllegalStateException("Invalid data recovered from the database for Part entity.", e);
        }
    }
}