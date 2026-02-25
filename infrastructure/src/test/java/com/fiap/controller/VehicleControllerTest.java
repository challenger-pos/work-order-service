package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.vehicle.Vehicle;
import com.fiap.dto.customer.CustomerResponse;
import com.fiap.dto.vehicle.CreateVehicleRequest;
import com.fiap.dto.vehicle.UpdateVehicleRequest;
import com.fiap.dto.vehicle.VehicleResponse;
import com.fiap.mapper.vehicle.VehicleMapper;
import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import com.fiap.usecase.vehicle.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VehicleController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehicleControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TokenService tokenService;
    @MockitoBean private ClientJwtService clientJwtService;
    @MockitoBean private UserEntityRepository userEntityRepository;

    @MockitoBean private CreateVehicleUseCase createVehicleUseCase;
    @MockitoBean private FindVehicleByIdUseCase findVehicleByIdUseCase;
    @MockitoBean private FindVehicleByPlateUseCase findVehicleByPlateUseCase;
    @MockitoBean private UpdateVehicleUseCase updateVehicleUseCase;
    @MockitoBean private DeleteVehicleUseCase deleteVehicleUseCase;
    @MockitoBean private VehicleMapper vehicleMapper;

    @Test
    void shouldCreateVehicleAndReturn201() throws Exception {
        UUID customerId = UUID.randomUUID();

        CreateVehicleRequest request = new CreateVehicleRequest(customerId, "ABC-1234", "Honda", "Civic", 2020);

        Vehicle vehicleDomain = new Vehicle();
        vehicleDomain.setId(UUID.randomUUID());

        CustomerResponse customerResponse = new CustomerResponse(
                customerId, "João", "joao@gmail.com", com.fiap.core.domain.customer.DocumentNumber.fromPersistence("98765432100")
        );

        VehicleResponse response = new VehicleResponse(
                vehicleDomain.getId(), customerResponse, "ABC-1234", "Honda", "Civic", 2020, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(vehicleMapper.toDomain(any(CreateVehicleRequest.class))).thenReturn(vehicleDomain);
        when(createVehicleUseCase.execute(any(Vehicle.class))).thenReturn(vehicleDomain);
        when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(response);

        mockMvc.perform(post("/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.brand").value("Honda"))
                .andExpect(jsonPath("$.customer.name").value("João"));
    }

    @Test
    void shouldFindVehicleByIdAndReturn200() throws Exception {
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Vehicle vehicleDomain = new Vehicle();
        vehicleDomain.setId(vehicleId);
        vehicleDomain.setLicensePlate("XYZ-9876");

        CustomerResponse customerResponse = new CustomerResponse(
                customerId, "Maria", "maria@gmail.com", com.fiap.core.domain.customer.DocumentNumber.fromPersistence("12345678909")
        );

        VehicleResponse response = new VehicleResponse(
                vehicleId, customerResponse, "XYZ-9876", "Toyota", "Corolla", 2022, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(findVehicleByIdUseCase.execute(vehicleId)).thenReturn(vehicleDomain);
        when(vehicleMapper.toResponse(vehicleDomain)).thenReturn(response);

        mockMvc.perform(get("/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleId.toString()))
                .andExpect(jsonPath("$.licensePlate").value("XYZ-9876"))
                .andExpect(jsonPath("$.model").value("Corolla"));
    }

    @Test
    void shouldFindVehicleByPlateAndReturn200() throws Exception {
        String plate = "XYZ-9876";
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Vehicle vehicleDomain = new Vehicle();
        vehicleDomain.setId(vehicleId);

        CustomerResponse customerResponse = new CustomerResponse(
                customerId, "Maria", "maria@gmail.com", com.fiap.core.domain.customer.DocumentNumber.fromPersistence("12345678909")
        );

        VehicleResponse response = new VehicleResponse(
                vehicleId, customerResponse, plate, "Toyota", "Corolla", 2022, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(findVehicleByPlateUseCase.execute(plate)).thenReturn(vehicleDomain);
        when(vehicleMapper.toResponse(vehicleDomain)).thenReturn(response);

        mockMvc.perform(get("/v1/vehicles/by-plate").param("plate", plate))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value(plate))
                .andExpect(jsonPath("$.brand").value("Toyota"));
    }

    @Test
    void shouldUpdateVehicleAndReturn200() throws Exception {
        UUID vehicleId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        UpdateVehicleRequest request = new UpdateVehicleRequest(customerId, "DEF-5678", "Ford", "Focus", 2023);

        Vehicle vehicleDomain = new Vehicle();
        vehicleDomain.setId(vehicleId);

        CustomerResponse customerResponse = new CustomerResponse(
                customerId, "Pedro", "pedro@gmail.com", com.fiap.core.domain.customer.DocumentNumber.fromPersistence("98765432100")
        );

        VehicleResponse response = new VehicleResponse(
                vehicleId, customerResponse, "DEF-5678", "Ford", "Focus", 2023, OffsetDateTime.now(), OffsetDateTime.now()
        );

        when(vehicleMapper.toDomain(eq(vehicleId), any(UpdateVehicleRequest.class))).thenReturn(vehicleDomain);
        when(updateVehicleUseCase.execute(any(Vehicle.class))).thenReturn(vehicleDomain);
        when(vehicleMapper.toResponse(any(Vehicle.class))).thenReturn(response);

        mockMvc.perform(put("/v1/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("DEF-5678"))
                .andExpect(jsonPath("$.year").value(2023));
    }

    @Test
    void shouldDeleteVehicleAndReturn204() throws Exception {
        UUID vehicleId = UUID.randomUUID();

        doNothing().when(deleteVehicleUseCase).execute(vehicleId);

        mockMvc.perform(delete("/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNoContent());
    }
}