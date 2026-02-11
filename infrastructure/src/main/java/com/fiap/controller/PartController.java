package com.fiap.controller;

import com.fiap.core.domain.part.Part;
import com.fiap.core.exception.BusinessRuleException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.dto.part.CreatePartRequest;
import com.fiap.dto.part.PartResponse;
import com.fiap.dto.part.UpdatePartRequest;
import com.fiap.mapper.part.PartMapper;
import com.fiap.usecase.part.CreatePartUseCase;
import com.fiap.usecase.part.DeletePartUseCase;
import com.fiap.usecase.part.FindPartByIdUseCase;
import com.fiap.usecase.part.UpdatePartUseCase;
import datadog.trace.api.Trace;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/parts")
public class PartController {

    private final CreatePartUseCase createPartUseCase;
    private final FindPartByIdUseCase findPartByIdUseCase;
    private final UpdatePartUseCase updatePartUseCase;
    private final DeletePartUseCase deletePartUseCase;
    private final PartMapper partMapper;

    public PartController(CreatePartUseCase createPartUseCase, FindPartByIdUseCase findPartByIdUseCase, UpdatePartUseCase updatePartUseCase, DeletePartUseCase deletePartUseCase, PartMapper partMapper) {
        this.createPartUseCase = createPartUseCase;
        this.findPartByIdUseCase = findPartByIdUseCase;
        this.updatePartUseCase = updatePartUseCase;
        this.deletePartUseCase = deletePartUseCase;
        this.partMapper = partMapper;
    }

    @Operation(summary = "Cria uma nova peça")
    @ApiResponse(responseCode = "201", description = "Peça criada com sucesso")
    @PostMapping
    public ResponseEntity<PartResponse> createPart(@Valid @RequestBody CreatePartRequest request) throws BusinessRuleException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "create");
        }
        Part newPart = createPartUseCase.execute(partMapper.toDomain(request));
        if (span != null && newPart != null) {
            span.setTag("part.id", newPart.getId().toString());
            // TODO [MS Estoque] REMOVER span tag de stock - estoque gerenciado pelo MS Estoque
            span.setTag("stock.quantity", String.valueOf(newPart.getStock().getStockQuantity()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(partMapper.toResponse(newPart));
    }

    @Operation(summary = "Busca uma peça pelo ID")
    @ApiResponse(responseCode = "200", description = "Peça encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<PartResponse> findPartById(@PathVariable UUID id) throws NotFoundException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "findById");
            span.setTag("part.id", id.toString());
        }
        Part part = findPartByIdUseCase.execute(id);
        if (span != null && part != null) {
            // TODO [MS Estoque] REMOVER span tag de stock - estoque gerenciado pelo MS Estoque
            span.setTag("stock.quantity", String.valueOf(part.getStock().getStockQuantity()));
        }
        return ResponseEntity.ok(partMapper.toResponse(part));
    }

    @Operation(summary = "Atualiza uma peça existente")
    @ApiResponse(responseCode = "200", description = "Peça atualizada com sucesso")
    @PutMapping("/{id}")
    public ResponseEntity<PartResponse> updatePart(@PathVariable UUID id, @Valid @RequestBody UpdatePartRequest request) throws NotFoundException, BusinessRuleException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "update");
            span.setTag("part.id", id.toString());
        }
        Part partToUpdate = partMapper.toDomain(id, request);
        Part updatedPart = updatePartUseCase.execute(partToUpdate);
        if (span != null && updatedPart != null) {
            // TODO [MS Estoque] REMOVER span tag de stock - estoque gerenciado pelo MS Estoque
            span.setTag("stock.quantity", String.valueOf(updatedPart.getStock().getStockQuantity()));
        }
        return ResponseEntity.ok(partMapper.toResponse(updatedPart));
    }

    @Operation(summary = "Deleta uma peça pelo ID")
    @ApiResponse(responseCode = "204", description = "Peça deletada com sucesso")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePart(@PathVariable UUID id) throws NotFoundException, BusinessRuleException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "delete");
            span.setTag("part.id", id.toString());
        }
        deletePartUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}