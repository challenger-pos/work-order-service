package com.fiap.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fiap.core.domain.auth.LoginRequest;
import com.fiap.core.domain.auth.Token;
import com.fiap.core.exception.InvalidCredentialsException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.usecase.auth.LoginUseCase;
import com.fiap.security.jwt.TokenService;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.persistence.repository.user.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private LoginUseCase loginUseCase;

        @org.springframework.boot.test.mock.mockito.MockBean
        private TokenService tokenService;

        @org.springframework.boot.test.mock.mockito.MockBean
        private ClientJwtService clientJwtService;

        @org.springframework.boot.test.mock.mockito.MockBean
        private UserEntityRepository userEntityRepository;

    @Test
    void shouldLoginSuccessfullyAndReturn200() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "Password@123");
        Token token = new Token("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...");

        when(loginUseCase.execute(any(LoginRequest.class))).thenReturn(token);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void shouldReturnBadRequestWhenEmailIsNull() throws Exception {
        String invalidRequest = "{ \"password\": \"Password@123\" }";

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenPasswordIsNull() throws Exception {
        String invalidRequest = "{ \"email\": \"user@example.com\" }";

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnUnauthorizedWhenInvalidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("user@example.com", "wrongpassword");

        when(loginUseCase.execute(any(LoginRequest.class)))
                .thenThrow(new InvalidCredentialsException("Invalid credentials"));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() throws Exception {
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "Password@123");

        when(loginUseCase.execute(any(LoginRequest.class)))
                .thenThrow(new NotFoundException("User not found", "USER_NOT_FOUND"));

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldHandleEmptyEmailAndPassword() throws Exception {
        LoginRequest loginRequest = new LoginRequest("", "");

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldLoginWithValidCredentials() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin@fiap.com", "Admin@123");
        Token token = new Token("jwt_token_here");

        when(loginUseCase.execute(any(LoginRequest.class))).thenReturn(token);

        mockMvc.perform(post("/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }
}
