package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.user.User;
import com.fiap.core.domain.workorder.WorkOrder;
import com.fiap.core.domain.workorder.WorkOrderHistory;
import com.fiap.core.domain.workorder.WorkOrderStatus;
import com.fiap.dto.workorder.*;
import com.fiap.mapper.workorder.WorkOrderHistoryMapper;
import com.fiap.mapper.workorder.WorkOrderMapper;
import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import com.fiap.usecase.workorder.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = WorkOrderController.class)
@AutoConfigureMockMvc(addFilters = false)
class WorkOrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TokenService tokenService;
    @MockitoBean private ClientJwtService clientJwtService;
    @MockitoBean private UserEntityRepository userEntityRepository;

    @MockitoBean private CreateWorkOrderUseCase createWorkOrderUseCase;
    @MockitoBean private FindWorkOrderByIdUseCase findWorkOrderByIdUseCase;
    @MockitoBean private AssignedMechanicUseCase assignedMechanicUseCase;
    @MockitoBean private WorkOrderMapper workOrderMapper;
    @MockitoBean private UpdateStatusWorkOrderUseCase updateStatusWorkOrderUseCase;
    @MockitoBean private GetWorkOrderStatusUseCase getWorkOrderStatusUseCase;
    @MockitoBean private ApproveWorkOrderUseCase approveWorkOrderUseCase;
    @MockitoBean private RefuseWorkOrderUseCase refuseWorkOrderUseCase;
    @MockitoBean private AddItemsWorkOrderUseCase addItemsWorkOrderUseCase;
    @MockitoBean private CalculateAverageTimeWorkOrderUseCase calculateAverageTimeWorkOrderUseCase;
    @MockitoBean private ListWorkOrdersByStatusUseCase listWorkOrdersByStatusUseCase;
    @MockitoBean private GetWorkOrderHistoryUseCase getWorkOrderHistoryUseCase;
    @MockitoBean private WorkOrderHistoryMapper workOrderHistoryMapper;

    private WorkOrder defaultWorkOrderDomain;
    private WorkOrderResponse defaultWorkOrderResponse;
    private UUID workOrderId;

    @BeforeEach
    void setUp() {
        workOrderId = UUID.randomUUID();

        defaultWorkOrderDomain = new WorkOrder();
        defaultWorkOrderDomain.setId(workOrderId);
        defaultWorkOrderDomain.setStatus(WorkOrderStatus.RECEIVED);

        Customer mockCustomer = new Customer();
        mockCustomer.setId(UUID.randomUUID());
        defaultWorkOrderDomain.setCustomer(mockCustomer);

        User mockMechanic = new User();
        mockMechanic.setId(UUID.randomUUID());
        defaultWorkOrderDomain.setAssignedMechanic(mockMechanic);

        defaultWorkOrderResponse = new WorkOrderResponse(
                workOrderId, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                new BigDecimal("150.00"), List.of(), List.of(), "RECEIVED", LocalDateTime.now()
        );
    }

    @Test
    void shouldCreateWorkOrderAndReturn201() throws Exception {
        CreateWorkOrderRequest request = new CreateWorkOrderRequest(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), List.of(), List.of()
        );

        when(workOrderMapper.toDomain(any(CreateWorkOrderRequest.class))).thenReturn(defaultWorkOrderDomain);
        when(createWorkOrderUseCase.execute(any(WorkOrder.class))).thenReturn(defaultWorkOrderDomain);
        when(workOrderMapper.toResponse(any(WorkOrder.class))).thenReturn(defaultWorkOrderResponse);

        mockMvc.perform(post("/v1/work-orders/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void shouldFindWorkOrderByIdAndReturn200() throws Exception {
        when(findWorkOrderByIdUseCase.execute(workOrderId)).thenReturn(defaultWorkOrderDomain);
        when(workOrderMapper.toResponse(defaultWorkOrderDomain)).thenReturn(defaultWorkOrderResponse);

        mockMvc.perform(get("/v1/work-orders/{id}", workOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workOrderId.toString()));
    }

    @Test
    void shouldGetWorkOrdersListAndReturn200() throws Exception {
        when(listWorkOrdersByStatusUseCase.execute()).thenReturn(List.of(defaultWorkOrderDomain));
        when(workOrderMapper.toResponse(defaultWorkOrderDomain)).thenReturn(defaultWorkOrderResponse);

        mockMvc.perform(get("/v1/work-orders/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(workOrderId.toString()));
    }

    @Test
    void shouldAssignMechanicAndReturn200() throws Exception {
        WorkOrderAssignMechanicRequest request = new WorkOrderAssignMechanicRequest(UUID.randomUUID());

        mockMvc.perform(patch("/v1/work-orders/{id}/assign-mechanic", workOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Mecânico vinculado."));
    }

    @Test
    void shouldUpdateStatusAndReturn200() throws Exception {
        UpdateStatusWorkOrderRequest request = new UpdateStatusWorkOrderRequest("APPROVED");

        when(updateStatusWorkOrderUseCase.execute(eq(workOrderId), eq("APPROVED"))).thenReturn(defaultWorkOrderDomain);
        when(workOrderMapper.toResponse(defaultWorkOrderDomain)).thenReturn(defaultWorkOrderResponse);

        mockMvc.perform(patch("/v1/work-orders/{id}/status", workOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void shouldGetWorkOrderStatusAndReturn200() throws Exception {
        String documentNumber = "12345678909";
        when(getWorkOrderStatusUseCase.execute(workOrderId, documentNumber)).thenReturn(WorkOrderStatus.RECEIVED);

        mockMvc.perform(get("/v1/work-orders/{id}/status", workOrderId)
                        .requestAttr("documentNumber", documentNumber))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Recebido"));
    }

    @Test
    void shouldApproveWorkOrderAndReturn200() throws Exception {
        String documentNumber = "12345678909";

        mockMvc.perform(patch("/v1/work-orders/{id}/approve", workOrderId)
                        .requestAttr("documentNumber", documentNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("Ordem de Serviço aprovada."));
    }

    @Test
    void shouldRefuseWorkOrderAndReturn200() throws Exception {
        String documentNumber = "12345678909";

        mockMvc.perform(patch("/v1/work-orders/{id}/refuse", workOrderId)
                        .requestAttr("documentNumber", documentNumber))
                .andExpect(status().isOk())
                .andExpect(content().string("Ordem de Serviço recusada."));
    }

    @Test
    void shouldUpdateItemsAndReturn200() throws Exception {
        UpdateWorkOrderItemsRequest request = new UpdateWorkOrderItemsRequest(List.of(), List.of());

        when(workOrderMapper.toDomain(any(UpdateWorkOrderItemsRequest.class))).thenReturn(defaultWorkOrderDomain);
        when(addItemsWorkOrderUseCase.execute(eq(workOrderId), any(WorkOrder.class))).thenReturn(defaultWorkOrderDomain);
        when(workOrderMapper.toResponse(defaultWorkOrderDomain)).thenReturn(defaultWorkOrderResponse);

        mockMvc.perform(patch("/v1/work-orders/{id}/update-items", workOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(workOrderId.toString()));
    }

    @Test
    void shouldGetHistoryByCpfAndReturn200() throws Exception {
        String cpf = "12345678909";
        WorkOrderHistory history = new WorkOrderHistory();
        GetWorkOrderHistoryResponse historyResponse = new GetWorkOrderHistoryResponse(workOrderId, "RECEIVED", "notes", LocalDateTime.now());

        when(getWorkOrderHistoryUseCase.execute(cpf)).thenReturn(List.of(history));
        when(workOrderHistoryMapper.toResponse(List.of(history))).thenReturn(List.of(historyResponse));

        mockMvc.perform(get("/v1/work-orders/history/by-cpf/{cpfCnpj}", cpf))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workOrderId").value(workOrderId.toString()));
    }

    @Test
    void shouldCalculateAverageTimeAndReturn200() throws Exception {
        when(calculateAverageTimeWorkOrderUseCase.execute()).thenReturn("2.5 horas");

        mockMvc.perform(get("/v1/work-orders/calculate-average-time"))
                .andExpect(status().isOk())
                .andExpect(content().string("2.5 horas"));
    }
}