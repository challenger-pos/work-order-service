package com.fiap.gateway.auth;

import com.fiap.core.domain.auth.Token;
import com.fiap.core.domain.user.User;
import com.fiap.core.exception.InvalidCredentialsException;
import com.fiap.security.jwt.TokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthRepositoryGatewayTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthRepositoryGateway authRepositoryGateway;

    @Test
    void shouldAuthenticateUserSuccessfully() throws InvalidCredentialsException, com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("user@example.com");

        String password = "password123";
        String token = "jwt_token_here";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        when(tokenService.generateToken(user)).thenReturn(token);

        // Act
        Token result = authRepositoryGateway.authenticate(user, password);

        // Assert
        assertNotNull(result);
        assertEquals(token, result.accessToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService).generateToken(user);
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenAuthenticationFails() throws com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setEmail("user@example.com");

        String password = "wrongpassword";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new org.springframework.security.core.AuthenticationException("Bad credentials") {});

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authRepositoryGateway.authenticate(user, password);
        });

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    void shouldGenerateTokenAfterSuccessfulAuthentication() throws InvalidCredentialsException, com.fiap.core.exception.EmailException {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("admin@example.com");

        String password = "AdminPassword123!";
        String generatedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        when(tokenService.generateToken(user)).thenReturn(generatedToken);

        // Act
        Token result = authRepositoryGateway.authenticate(user, password);

        // Assert
        assertNotNull(result);
        assertEquals(generatedToken, result.accessToken());
        verify(tokenService).generateToken(user);
    }
}
