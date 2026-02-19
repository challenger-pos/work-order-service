package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.user.User;
import com.fiap.dto.user.CreateUserRequest;
import com.fiap.dto.user.UpdateUserRequest;
import com.fiap.dto.user.UserResponse;
import com.fiap.mapper.user.UserMapper;
import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import com.fiap.usecase.user.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TokenService tokenService;
    @MockitoBean private ClientJwtService clientJwtService;
    @MockitoBean private UserEntityRepository userEntityRepository;

    @MockitoBean private CreateUserUseCase createUserUseCase;
    @MockitoBean private UpdateUserUseCase updateUserUseCase;
    @MockitoBean private DeleteUserUseCase deleteUserUseCase;
    @MockitoBean private FindUserByIdUseCase findUserByIdUseCase;
    @MockitoBean private UserMapper userMapper;

    @Test
    void shouldCreateUserAndReturn201() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Carlos", "carlos@gmail.com", "MECHANIC", "Senha@123");
        User userDomain = new User();
        userDomain.setId(UUID.randomUUID());

        UserResponse response = new UserResponse(userDomain.getId(), "Carlos", "carlos@gmail.com");

        when(userMapper.toDomain(any(CreateUserRequest.class))).thenReturn(userDomain);
        when(createUserUseCase.execute(any(User.class))).thenReturn(userDomain);
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        mockMvc.perform(post("/v1/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Carlos"))
                .andExpect(jsonPath("$.email").value("carlos@gmail.com"));
    }

    @Test
    void shouldUpdateUserAndReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest(id, "Carlos Silva", "carlos.silva@gmail.com", "ADMIN", "NovaSenha@123");

        User userDomain = new User();
        userDomain.setId(id);

        UserResponse response = new UserResponse(id, "Carlos Silva", "carlos.silva@gmail.com");

        when(userMapper.toDomainUpdate(any(UpdateUserRequest.class))).thenReturn(userDomain);
        when(updateUserUseCase.execute(any(User.class))).thenReturn(userDomain);
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        mockMvc.perform(put("/v1/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Carlos Silva"));
    }

    @Test
    void shouldFindUserByIdAndReturn200() throws Exception {
        UUID id = UUID.randomUUID();

        User userDomain = new User();
        userDomain.setId(id);
        userDomain.setEmail("busca@gmail.com");

        UserResponse response = new UserResponse(id, "Usuario Busca", "busca@gmail.com");

        when(findUserByIdUseCase.execute(id)).thenReturn(Optional.of(userDomain));
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        mockMvc.perform(get("/v1/users/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Usuario Busca"));
    }

    @Test
    void shouldDeleteUserAndReturn204() throws Exception {
        UUID id = UUID.randomUUID();

        doNothing().when(deleteUserUseCase).execute(id);

        mockMvc.perform(delete("/v1/users/{id}", id))
                .andExpect(status().isNoContent());
    }
}