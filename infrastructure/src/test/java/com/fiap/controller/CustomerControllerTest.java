package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.customer.Customer;
import com.fiap.core.domain.customer.DocumentNumber;
import com.fiap.dto.customer.CreateCustomerRequest;
import com.fiap.dto.customer.CustomerResponse;
import com.fiap.gateway.user.UserRepositoryGateway;
import com.fiap.mapper.customer.CustomerMapper;
import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import com.fiap.usecase.customer.*;
import io.opentracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private ClientJwtService clientJwtService;

    @MockitoBean
    private UserEntityRepository userEntityRepository;

    @MockitoBean
    private UserRepositoryGateway userRepositoryGateway;

    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;

    @MockitoBean
    private FindCustomerByIdUseCase findCustomerByIdUseCase;

    @MockitoBean
    private FindCustomerByDocumentUseCase findCustomerByDocumentUseCase;

    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;

    @MockitoBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    @MockitoBean
    private ActiveCustomerUseCase activeCustomerUseCase;

    @MockitoBean
    private InactiveCustomerUseCase inactiveCustomerUseCase;

    @MockitoBean
    private CustomerMapper customerMapper;

    @MockitoBean
    private Tracer tracer;

    @Test
    void shouldCreateCustomerAndReturn201Created() throws Exception {
        CreateCustomerRequest request = new CreateCustomerRequest("Ana", "12345678909", "ana@gmail.com", "999999999");
        Customer customerDomain = new Customer();
        customerDomain.setId(UUID.randomUUID());

        CustomerResponse responseBody = new CustomerResponse(customerDomain.getId(), "Ana", "ana@gmail.com", DocumentNumber.of("12345678909"));

        when(customerMapper.toDomain(any(CreateCustomerRequest.class))).thenReturn(customerDomain);
        when(createCustomerUseCase.execute(any(Customer.class))).thenReturn(customerDomain);
        when(customerMapper.toResponse(any(Customer.class))).thenReturn(responseBody);

        mockMvc.perform(post("/v1/customer/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(customerDomain.getId().toString()))
                .andExpect(jsonPath("$.name").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@gmail.com"));
    }

    @Test
    void shouldReturnCustomerAndStatus200OkWhenIdExists() throws Exception {
        UUID id = UUID.randomUUID();
        Customer customerDomain = new Customer();
        customerDomain.setId(id);

        CustomerResponse responseBody = new CustomerResponse(id, "João", "joao@gmail.com", DocumentNumber.of("98765432100"));

        when(findCustomerByIdUseCase.execute(id)).thenReturn(customerDomain);
        when(customerMapper.toResponse(customerDomain)).thenReturn(responseBody);

        mockMvc.perform(get("/v1/customer/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.documentNumber.value").value("98765432100"));
    }
}