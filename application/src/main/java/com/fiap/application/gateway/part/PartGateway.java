package com.fiap.application.gateway.part;

import com.fiap.core.domain.part.Part;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PartGateway {
    Part create(Part part);
    Part update(Part part);
    Optional<Part> findById(UUID id);
    List<Part> findByIds(List<UUID> ids);
    void delete(UUID id);
    boolean existsById(UUID id);
}