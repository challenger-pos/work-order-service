package com.fiap.gateway.part;

import com.fiap.application.gateway.part.PartGateway;
import com.fiap.core.domain.part.Part;
import com.fiap.mapper.part.PartMapper;
import com.fiap.persistence.entity.part.PartEntity;
import com.fiap.persistence.repository.part.PartEntityRepository;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class PartRepositoryGateway implements PartGateway {

    private final PartEntityRepository partEntityRepository;
    private final PartMapper partMapper;

    public PartRepositoryGateway(PartEntityRepository partEntityRepository, PartMapper partMapper) {
        this.partEntityRepository = partEntityRepository;
        this.partMapper = partMapper;
    }

    @Override
    public Part create(Part part) {
        PartEntity entity = partMapper.toEntity(part);
        PartEntity savedEntity = partEntityRepository.save(entity);
        return partMapper.toDomain(savedEntity);
    }

    @Override
    public Part update(Part part) {
        PartEntity entity = partMapper.toEntity(part);
        PartEntity savedEntity = partEntityRepository.save(entity);
        return partMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Part> findById(UUID id) {
        return partEntityRepository.findById(id).map(partMapper::toDomain);
    }

    @Override
    public List<Part> findByIds(List<UUID> ids) {
        return partEntityRepository.findAllById(ids).stream()
                .map(partMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        partEntityRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return partEntityRepository.existsById(id);
    }
}