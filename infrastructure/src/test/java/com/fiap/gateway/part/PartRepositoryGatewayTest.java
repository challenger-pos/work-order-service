package com.fiap.gateway.part;

import com.fiap.core.domain.part.Part;
import com.fiap.mapper.part.PartMapper;
import com.fiap.persistence.entity.part.PartEntity;
import com.fiap.persistence.repository.part.PartEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartRepositoryGatewayTest {

    @Mock
    private PartEntityRepository partEntityRepository;

    @Mock
    private PartMapper partMapper;

    @InjectMocks
    private PartRepositoryGateway partRepositoryGateway;

    @Test
    void shouldCreatePartSuccessfully() {
        // Arrange
        UUID partId = UUID.randomUUID();
        Part part = new Part();
        part.setId(partId);

        PartEntity partEntity = new PartEntity();
        partEntity.setId(partId);

        when(partMapper.toEntity(part)).thenReturn(partEntity);
        when(partEntityRepository.save(partEntity)).thenReturn(partEntity);
        when(partMapper.toDomain(partEntity)).thenReturn(part);

        // Act
        Part result = partRepositoryGateway.create(part);

        // Assert
        assertNotNull(result);
        assertEquals(partId, result.getId());
        verify(partMapper).toEntity(part);
        verify(partEntityRepository).save(partEntity);
    }

    @Test
    void shouldUpdatePartSuccessfully() {
        // Arrange
        UUID partId = UUID.randomUUID();
        Part part = new Part();
        part.setId(partId);

        PartEntity partEntity = new PartEntity();
        partEntity.setId(partId);

        when(partMapper.toEntity(part)).thenReturn(partEntity);
        when(partEntityRepository.save(partEntity)).thenReturn(partEntity);
        when(partMapper.toDomain(partEntity)).thenReturn(part);

        // Act
        Part result = partRepositoryGateway.update(part);

        // Assert
        assertNotNull(result);
        verify(partEntityRepository).save(partEntity);
    }

    @Test
    void shouldFindPartByIdSuccessfully() {
        // Arrange
        UUID partId = UUID.randomUUID();
        Part part = new Part();
        part.setId(partId);

        PartEntity partEntity = new PartEntity();
        partEntity.setId(partId);

        when(partEntityRepository.findById(partId)).thenReturn(Optional.of(partEntity));
        when(partMapper.toDomain(partEntity)).thenReturn(part);

        // Act
        Optional<Part> result = partRepositoryGateway.findById(partId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(partId, result.get().getId());
    }

    @Test
    void shouldReturnEmptyWhenPartNotFound() {
        // Arrange
        UUID partId = UUID.randomUUID();
        when(partEntityRepository.findById(partId)).thenReturn(Optional.empty());

        // Act
        Optional<Part> result = partRepositoryGateway.findById(partId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void shouldFindMultiplePartsByIds() {
        // Arrange
        UUID partId1 = UUID.randomUUID();
        UUID partId2 = UUID.randomUUID();
        List<UUID> ids = Arrays.asList(partId1, partId2);

        Part part1 = new Part();
        part1.setId(partId1);

        Part part2 = new Part();
        part2.setId(partId2);

        PartEntity entity1 = new PartEntity();
        entity1.setId(partId1);

        PartEntity entity2 = new PartEntity();
        entity2.setId(partId2);

        when(partEntityRepository.findAllById(ids)).thenReturn(Arrays.asList(entity1, entity2));
        when(partMapper.toDomain(entity1)).thenReturn(part1);
        when(partMapper.toDomain(entity2)).thenReturn(part2);

        // Act
        List<Part> results = partRepositoryGateway.findByIds(ids);

        // Assert
        assertEquals(2, results.size());
        verify(partEntityRepository).findAllById(ids);
    }
}
