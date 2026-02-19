package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.part.Part;
import com.fiap.dto.part.CreatePartRequest;
import com.fiap.dto.part.PartResponse;
import com.fiap.dto.part.UpdatePartRequest;
import com.fiap.mapper.part.PartMapper;
import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import com.fiap.usecase.part.CreatePartUseCase;
import com.fiap.usecase.part.DeletePartUseCase;
import com.fiap.usecase.part.FindPartByIdUseCase;
import com.fiap.usecase.part.UpdatePartUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = PartController.class)
@AutoConfigureMockMvc(addFilters = false)
class PartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TokenService tokenService;
    @MockitoBean private ClientJwtService clientJwtService;
    @MockitoBean private UserEntityRepository userEntityRepository;

    @MockitoBean private CreatePartUseCase createPartUseCase;
    @MockitoBean private FindPartByIdUseCase findPartByIdUseCase;
    @MockitoBean private UpdatePartUseCase updatePartUseCase;
    @MockitoBean private DeletePartUseCase deletePartUseCase;
    @MockitoBean private PartMapper partMapper;

    @Test
    void shouldCreatePartAndReturn201() throws Exception {
        CreatePartRequest request = new CreatePartRequest("Filtro de Óleo", "Filtro XYZ", new BigDecimal("50.00"));

        Part partDomain = new Part();
        partDomain.setId(UUID.randomUUID());

        PartResponse response = new PartResponse(
                partDomain.getId(),
                "Filtro de Óleo",
                "Filtro XYZ",
                new BigDecimal("50.00"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(partMapper.toDomain(any(CreatePartRequest.class))).thenReturn(partDomain);
        when(createPartUseCase.execute(any(Part.class))).thenReturn(partDomain);
        when(partMapper.toResponse(any(Part.class))).thenReturn(response);

        mockMvc.perform(post("/v1/parts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Filtro de Óleo"))
                .andExpect(jsonPath("$.price").value(50.00));
    }

    @Test
    void shouldFindPartByIdAndReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        Part partDomain = new Part();
        partDomain.setId(id);

        PartResponse response = new PartResponse(
                id,
                "Pneu",
                "Pneu Aro 15",
                new BigDecimal("350.00"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(findPartByIdUseCase.execute(id)).thenReturn(partDomain);
        when(partMapper.toResponse(partDomain)).thenReturn(response);

        mockMvc.perform(get("/v1/parts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pneu"))
                .andExpect(jsonPath("$.price").value(350.00));
    }

    @Test
    void shouldUpdatePartAndReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdatePartRequest request = new UpdatePartRequest("Vela de Ignição", "Vela NGK", new BigDecimal("120.00"));

        Part partDomain = new Part();
        partDomain.setId(id);

        PartResponse response = new PartResponse(
                id,
                "Vela de Ignição",
                "Vela NGK",
                new BigDecimal("120.00"),
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );

        when(partMapper.toDomain(eq(id), any(UpdatePartRequest.class))).thenReturn(partDomain);
        when(updatePartUseCase.execute(any(Part.class))).thenReturn(partDomain);
        when(partMapper.toResponse(any(Part.class))).thenReturn(response);

        mockMvc.perform(put("/v1/parts/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Vela de Ignição"))
                .andExpect(jsonPath("$.price").value(120.00));
    }

    @Test
    void shouldDeletePartAndReturn204() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(deletePartUseCase).execute(id);

        mockMvc.perform(delete("/v1/parts/{id}", id))
                .andExpect(status().isNoContent());
    }
}