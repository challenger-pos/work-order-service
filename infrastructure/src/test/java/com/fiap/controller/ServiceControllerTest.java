package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.service.Service;
import com.fiap.dto.service.CreateServiceRequest;
import com.fiap.dto.service.ServiceResponse;
import com.fiap.dto.service.UpdateServiceRequest;
import com.fiap.mapper.service.ServiceMapper;
import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import com.fiap.usecase.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ServiceController.class)
@AutoConfigureMockMvc(addFilters = false)
class ServiceControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TokenService tokenService;
    @MockitoBean private ClientJwtService clientJwtService;
    @MockitoBean private UserEntityRepository userEntityRepository;

    @MockitoBean private CreateServiceUseCase createServiceUseCase;
    @MockitoBean private FindServiceByIdUseCase findServiceByIdUseCase;
    @MockitoBean private FindServicesByIdsUseCase findServicesByIdsUseCase;
    @MockitoBean private UpdateServiceUseCase updateServiceUseCase;
    @MockitoBean private DeleteServiceUseCase deleteServiceUseCase;
    @MockitoBean private ServiceMapper serviceMapper;

    @Test
    void shouldCreateServiceAndReturn201() throws Exception {
        CreateServiceRequest request = new CreateServiceRequest("Troca de Óleo", "Troca completa", new BigDecimal("100.00"), 60);
        Service serviceDomain = new Service();
        serviceDomain.setId(UUID.randomUUID());

        ServiceResponse response = new ServiceResponse(
                serviceDomain.getId(), "Troca de Óleo", "Troca completa", new BigDecimal("100.00"), 60, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(serviceMapper.toDomain(any(CreateServiceRequest.class))).thenReturn(serviceDomain);
        when(createServiceUseCase.execute(any(Service.class))).thenReturn(serviceDomain);
        when(serviceMapper.toResponse(any(Service.class))).thenReturn(response);

        mockMvc.perform(post("/v1/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Troca de Óleo"))
                .andExpect(jsonPath("$.description").value("Troca completa"));
    }

    @Test
    void shouldFindServiceByIdAndReturn200() throws Exception {
        UUID serviceId = UUID.randomUUID();
        Service serviceDomain = new Service();
        serviceDomain.setId(serviceId);

        ServiceResponse response = new ServiceResponse(
                serviceId, "Alinhamento", "Alinhamento 3D", new BigDecimal("80.00"), 45, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(findServiceByIdUseCase.execute(serviceId)).thenReturn(serviceDomain);
        when(serviceMapper.toResponse(serviceDomain)).thenReturn(response);

        mockMvc.perform(get("/v1/services/{id}", serviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(serviceId.toString()))
                .andExpect(jsonPath("$.name").value("Alinhamento"));
    }

    @Test
    void shouldFindServicesByIdsAndReturn200() throws Exception {
        UUID serviceId1 = UUID.randomUUID();
        UUID serviceId2 = UUID.randomUUID();
        List<UUID> ids = List.of(serviceId1, serviceId2);

        Service serviceDomain = new Service();
        serviceDomain.setId(serviceId1);

        ServiceResponse response1 = new ServiceResponse(
                serviceId1, "Alinhamento", "Alinhamento 3D", new BigDecimal("80.00"), 45, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(findServicesByIdsUseCase.execute(ids)).thenReturn(List.of(serviceDomain));
        when(serviceMapper.toResponse(serviceDomain)).thenReturn(response1);

        mockMvc.perform(get("/v1/services")
                        .param("ids", serviceId1.toString() + "," + serviceId2.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(serviceId1.toString()))
                .andExpect(jsonPath("$[0].name").value("Alinhamento"));
    }

    @Test
    void shouldUpdateServiceAndReturn200() throws Exception {
        UUID serviceId = UUID.randomUUID();
        UpdateServiceRequest request = new UpdateServiceRequest("Balanceamento", "Balanceamento das 4 rodas", new BigDecimal("120.00"), 50);

        Service serviceDomain = new Service();
        serviceDomain.setId(serviceId);

        ServiceResponse response = new ServiceResponse(
                serviceId, "Balanceamento", "Balanceamento das 4 rodas", new BigDecimal("120.00"), 50, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(serviceMapper.toDomain(eq(serviceId), any(UpdateServiceRequest.class))).thenReturn(serviceDomain);
        when(updateServiceUseCase.execute(any(Service.class))).thenReturn(serviceDomain);
        when(serviceMapper.toResponse(any(Service.class))).thenReturn(response);

        mockMvc.perform(put("/v1/services/{id}", serviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Balanceamento"))
                .andExpect(jsonPath("$.basePrice").value(120.00));
    }

    @Test
    void shouldDeleteServiceAndReturn204() throws Exception {
        UUID serviceId = UUID.randomUUID();

        doNothing().when(deleteServiceUseCase).execute(serviceId);

        mockMvc.perform(delete("/v1/services/{id}", serviceId))
                .andExpect(status().isNoContent());
    }
}