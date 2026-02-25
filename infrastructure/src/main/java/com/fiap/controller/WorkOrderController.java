package com.fiap.controller;

import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.exception.*;
import com.fiap.dto.workorder.*;
import com.fiap.mapper.workorder.WorkOrderHistoryMapper;
import com.fiap.mapper.workorder.WorkOrderMapper;
import com.fiap.dto.workorder.GetWorkOrderHistoryResponse;
import com.fiap.persistence.entity.workOrder.WorkOrderEntity;
import com.fiap.persistence.repository.workorder.WorkOrderHistoryRepository;
import com.fiap.persistence.repository.workorder.WorkOrderRepository;
import com.fiap.usecase.workorder.*;
import com.timgroup.statsd.StatsDClient;
import datadog.trace.api.Trace;
import io.opentracing.Span;
import io.opentracing.Tracer;
import io.opentracing.util.GlobalTracer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/work-orders")
public class WorkOrderController {

    private static final Logger logger = LoggerFactory.getLogger(WorkOrderController.class);
    private final CreateWorkOrderUseCase createWorkOrderUseCase;
    private final FindWorkOrderByIdUseCase findWorkOrderByIdUseCase;
    private final AssignedMechanicUseCase assignedMechanicUseCase;
    private final WorkOrderMapper workOrderMapper;
    private final UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase;
    private final GetWorkOrderStatusUseCase getWorkOrderStatusUseCase;
    private final ApproveWorkOrderUseCase approveWorkOrderUseCase;
    private final RefuseWorkOrderUseCase refuseWorkOrderUseCase;
    private final AddItemsWorkOrderUseCase addItemsWorkOrderUseCase;
    private final CalculateAverageTimeWorkOrderUseCase calculateAverageTimeWorkOrderUseCase;
    private final ListWorkOrdersByStatusUseCase listWorkOrdersByStatusUseCase;
    private final GetWorkOrderHistoryUseCase getWorkOrderHistoryUseCase;
    private final WorkOrderHistoryMapper workOrderHistoryMapper;

    public WorkOrderController(CreateWorkOrderUseCase createWorkOrderUseCase, FindWorkOrderByIdUseCase findWorkOrderByIdUseCase, AssignedMechanicUseCase assignedMechanicUseCase, WorkOrderMapper workOrderMapper, UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase, GetWorkOrderStatusUseCase getWorkOrderStatusUseCase, ApproveWorkOrderUseCase approveWorkOrderUseCase, RefuseWorkOrderUseCase refuseWorkOrderUseCase, AddItemsWorkOrderUseCase addItemsWorkOrderUseCase, CalculateAverageTimeWorkOrderUseCase calculateAverageTimeWorkOrderUseCase, ListWorkOrdersByStatusUseCase listWorkOrdersByStatusUseCase, GetWorkOrderHistoryUseCase getWorkOrderHistoryUseCase,
                               WorkOrderHistoryMapper workOrderHistoryMapper) {
        this.createWorkOrderUseCase = createWorkOrderUseCase;
        this.findWorkOrderByIdUseCase = findWorkOrderByIdUseCase;
        this.assignedMechanicUseCase = assignedMechanicUseCase;
        this.workOrderMapper = workOrderMapper;
        this.updateStatusWorkOrderUseCase = updateStatusWorkOrderUseCase;
        this.getWorkOrderStatusUseCase = getWorkOrderStatusUseCase;
        this.approveWorkOrderUseCase = approveWorkOrderUseCase;
        this.refuseWorkOrderUseCase = refuseWorkOrderUseCase;
        this.addItemsWorkOrderUseCase = addItemsWorkOrderUseCase;
        this.calculateAverageTimeWorkOrderUseCase = calculateAverageTimeWorkOrderUseCase;
        this.listWorkOrdersByStatusUseCase = listWorkOrdersByStatusUseCase;
        this.getWorkOrderHistoryUseCase = getWorkOrderHistoryUseCase;
        this.workOrderHistoryMapper = workOrderHistoryMapper;
    }

    @Operation(
            summary = "Cria uma nova ordem de serviço",
            description = "Endpoint para criar uma nova ordem de serviço.")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "201", description = "Ordem de serviço criada com sucesso.") })
    @PostMapping("/create")
    public ResponseEntity<WorkOrderResponse> createWorkOrder(@RequestBody CreateWorkOrderRequest request) throws NotFoundException, BadRequestException, BusinessRuleException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "create");
            if (request.customerId() != null) {
                span.setTag("workorder.customer_id", request.customerId().toString());
            }
        }
        var workOrder = createWorkOrderUseCase.execute(workOrderMapper.toDomain(request));
        if (span != null && workOrder != null) {
            span.setTag("workorder.id", workOrder.getId().toString());
            span.setTag("workorder.status", workOrder.getStatus().getDescription());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(workOrderMapper.toResponse(workOrder));
    }

    @Operation(
            summary = "Busca uma ordem de serviço pelo ID",
            description = "Endpoint para buscar uma ordem de serviço pelo ID.")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Ordem de serviço encontrada com sucesso.") })
    @GetMapping("/{id}")
    public ResponseEntity<WorkOrderResponse> findWorkOrderById(@PathVariable UUID id) throws NotFoundException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "findById");
            span.setTag("workorder.id", id.toString());
        }
        var workOrder = findWorkOrderByIdUseCase.execute(id);
        if (span != null && workOrder != null) {
            span.setTag("workorder.status", workOrder.getStatus().getDescription());
            if (workOrder.getCustomer().getId() != null) {
                span.setTag("workorder.customer_id", workOrder.getCustomer().getId().toString());
            }
            if (workOrder.getAssignedMechanic().getId() != null) {
                span.setTag("workorder.mechanic_id", workOrder.getAssignedMechanic().getId().toString());
            }
        }
        return ResponseEntity.ok().body(workOrderMapper.toResponse(workOrder));
    }

    @Operation(
            summary = "Retorna Lista de Ordem de Serviço",
            description = "Endpoint para retornar OS, ordenadas por status")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Ordens de Serviço retornadas com sucesso.") })
    @GetMapping("/list")
    public ResponseEntity<List<WorkOrderResponse>> getWorkOrders() {
        List<WorkOrder> workOrders = listWorkOrdersByStatusUseCase.execute();

        return ResponseEntity.ok().body(workOrders.stream().map(workOrderMapper::toResponse).collect(Collectors.toList()));
    }

    @Operation(
            summary = "Vincula um mecânico a uma ordem de serviço",
            description = "Endpoint para vincular um mecânico a uma ordem de serviço pelo ID da OS e do Mecânico")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Mecânico vinculado com sucesso.") })
    @PatchMapping("/{id}/assign-mechanic")
    public ResponseEntity<String> assignMechanic(@PathVariable UUID id, @RequestBody WorkOrderAssignMechanicRequest assignMechanicRequest) throws NotFoundException, BadRequestException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "assignMechanic");
            span.setTag("workorder.id", id.toString());
            if (assignMechanicRequest.mechanicId() != null) {
                span.setTag("workorder.mechanic_id", assignMechanicRequest.mechanicId().toString());
            }
        }
        assignedMechanicUseCase.execute(id, assignMechanicRequest.mechanicId());
        return ResponseEntity.ok("Mecânico vinculado.");
    }

    @Operation(
            summary = "Atualiza o status de uma ordem de serviço",
            description = "Endpoint para alterar o status de uma ordem de serviço pelo ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso."),
            @ApiResponse(responseCode = "404", description = "Ordem de serviço não encontrada."),
            @ApiResponse(responseCode = "400", description = "Status inválido informado.")
    })
    @PatchMapping("/{id}/status")
    public ResponseEntity<WorkOrderResponse> updateStatus(
            @PathVariable UUID id,
            @RequestBody UpdateStatusWorkOrderRequest request
    ) throws NotFoundException, BadRequestException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "updateStatus");
            span.setTag("workorder.id", id.toString());
            if (request.status() != null) {
                span.setTag("workorder.status", request.status());
            }
        }
        var workOrder = updateStatusWorkOrderUseCase.execute(id, request.status());
        if (span != null && workOrder != null) {
            span.setTag("workorder.status", workOrder.getStatus().getDescription());
        }
        return ResponseEntity.ok(workOrderMapper.toResponse(workOrder));
    }

    @Operation(
            summary = "Consulta o status da ordem de serviço pelo ID",
            description = "Endpoint para consultar apenas o status atual de uma ordem de serviço.")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Status da ordem de serviço obtido com sucesso.") })
    @GetMapping("/{id}/status")
    public ResponseEntity<WorkOrderStatusResponse> getWorkOrderStatus(@PathVariable UUID id, HttpServletRequest request) throws NotFoundException, ForbiddenException, UnauthorizedException {

        String documentNumber = (String) request.getAttribute("documentNumber");

        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "getStatus");
            span.setTag("workorder.id", id.toString());
        }

        var status = getWorkOrderStatusUseCase.execute(id, documentNumber);

        if (span != null && status != null) {
            span.setTag("workorder.status", status.getDescription());
        }

        return ResponseEntity.ok().body(new WorkOrderStatusResponse(id, status.getDescription()));
    }

    @Operation(
            summary = "Aprova uma ordem de serviço",
            description = "Endpoint para aprovar uma ordem de serviço pelo ID")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Ordem de serviço aprovada com sucesso.") })
    @PatchMapping("/{id}/approve")
    public ResponseEntity<String> approveWorkOrder(@PathVariable UUID id, HttpServletRequest request) throws NotFoundException, BadRequestException, UnauthorizedException, ForbiddenException {
        String documentNumber = (String) request.getAttribute("documentNumber");
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "approve");
            span.setTag("workorder.id", id.toString());
        }
        approveWorkOrderUseCase.execute(id, documentNumber);
        return ResponseEntity.ok("Ordem de Serviço aprovada.");
    }

    @Operation(
            summary = "Reprova uma ordem de serviço",
            description = "Endpoint para reprovar uma ordem de serviço pelo ID")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Ordem de serviço recusada com sucesso.") })
    @PatchMapping("/{id}/refuse")
    public ResponseEntity<String> refuseWorkOrder(@PathVariable UUID id, HttpServletRequest request) throws NotFoundException, BadRequestException, BusinessRuleException, UnauthorizedException, ForbiddenException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "refuse");
            span.setTag("workorder.id", id.toString());
        }
        String documentNumber = (String) request.getAttribute("documentNumber");

        refuseWorkOrderUseCase.execute(id, documentNumber);
        return ResponseEntity.ok("Ordem de Serviço recusada.");
    }

    @Operation(
            summary = "Adiciona novos itens para a Ordem de Serviço",
            description = "Endpoint para adicionar novas peças/insumos para a ordem de serviço")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Itens adicionados com sucesso.") })
    @PatchMapping("/{id}/update-items")
    public ResponseEntity<WorkOrderResponse> updateItems(@PathVariable UUID id, @RequestBody UpdateWorkOrderItemsRequest updateWorkOrderItemsRequest) throws BadRequestException, BusinessRuleException, NotFoundException {
        Span span = GlobalTracer.get().activeSpan();
        if (span != null) {
            span.setTag("operation.type", "updateItems");
            span.setTag("workorder.id", id.toString());
        }
        var workOrder = addItemsWorkOrderUseCase.execute(id, workOrderMapper.toDomain(updateWorkOrderItemsRequest));
        if (span != null && workOrder != null) {
            span.setTag("workorder.status", workOrder.getStatus().getDescription());
        }
        return ResponseEntity.status(HttpStatus.OK).body(workOrderMapper.toResponse(workOrder));
    }

    @Operation(
            summary = "Busca o histórico de ordens de serviço por CPF/CNPJ",
            description = "Retorna a linha do tempo de status de todas as OS do cliente (ordenado por data)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Histórico obtido com sucesso.")
    })
    @GetMapping("/history/by-cpf/{cpfCnpj}")
    public ResponseEntity<List<GetWorkOrderHistoryResponse>> getHistoryByCpf(@PathVariable String cpfCnpj) throws NotFoundException {
        var histories = getWorkOrderHistoryUseCase.execute(cpfCnpj);
        return ResponseEntity.ok(workOrderHistoryMapper.toResponse(histories));
    }


    @Operation(
            summary = "Calcula o tempo médio de conclusão das ordens de serviço",
            description = "Endpoint para calcular o tempo médio de conclusão das ordens de serviço")
    @ApiResponses(
            value = { @ApiResponse(responseCode = "200", description = "Tempo médio calculado com sucesso.") })
    @GetMapping("/calculate-average-time")
    public ResponseEntity<String> calculateAverageTime() {
        String average = calculateAverageTimeWorkOrderUseCase.execute();
        return ResponseEntity.ok(average);

    }

}
