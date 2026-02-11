package com.fiap.application.gateway.part;

import com.fiap.core.domain.part.Part;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartGateway {
    Part create(Part part);
    // TODO [MS Estoque] REMOVER saveAll() - este metodo era usado exclusivamente pela OS para salvar alteracoes de estoque.
    //   Sera removido quando stock migrar para MS Estoque.
    void saveAll(List<Part> parts);
    Part update(Part part);
    Optional<Part> findById(UUID id);
    List<Part> findByIds(List<UUID> ids);
    void delete(UUID id);
    boolean existsById(UUID id);
}