package com.fiap.security;

import com.fiap.persistence.repository.user.UserEntityRepository;
import com.fiap.security.jwt.ClientJwtService;
import com.fiap.security.jwt.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityFilterTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private ClientJwtService clientJwtService;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private FilterChain filterChain;

    private SecurityFilter securityFilter;

    @BeforeEach
    void setUp() {
        securityFilter = new SecurityFilter(tokenService, clientJwtService, userEntityRepository);
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldProcessRequestWithoutToken() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldProcessRequestWithBearerTokenButNoEmail() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "valid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(null);
        when(clientJwtService.getDocumentFromToken(token)).thenReturn("12345678901");

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals("12345678901", request.getAttribute("documentNumber"));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldProcessRequestWithInvalidBearerToken() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "invalid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(null);
        when(clientJwtService.getDocumentFromToken(token)).thenThrow(new RuntimeException("Invalid token"));

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(401, response.getStatus());
        assertEquals("UTF-8", response.getCharacterEncoding());
        assertEquals("text/plain; charset=UTF-8", response.getContentType());
    }

    @Test
    void shouldProcessRequestWithEmptyAuthorizationHeader() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "");

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldNotProcessTokenWithoutBearerPrefix() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Basic sometoken");

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldHandleEmptyEmailFromToken() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "valid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn("");
        when(clientJwtService.getDocumentFromToken(token)).thenReturn("12345678901");

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals("12345678901", request.getAttribute("documentNumber"));
    }

    @Test
    void shouldHandleNullEmailFromToken() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "valid-jwt-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(null);
        when(clientJwtService.getDocumentFromToken(token)).thenReturn("12345678901");

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals("12345678901", request.getAttribute("documentNumber"));
    }

    @Test
    void shouldSetDocumentNumberWhenClientTokenIsProvided() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "client-jwt-token";
        String documentNumber = "98765432100";
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(null);
        when(clientJwtService.getDocumentFromToken(token)).thenReturn(documentNumber);

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(documentNumber, request.getAttribute("documentNumber"));
        assertEquals(200, response.getStatus());
    }

    @Test
    void shouldHandleClientJwtExceptionWithProperResponse() throws ServletException, IOException {
        // Arrange
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String token = "expired-token";
        request.addHeader("Authorization", "Bearer " + token);

        when(tokenService.validateToken(token)).thenReturn(null);
        when(clientJwtService.getDocumentFromToken(token)).thenThrow(new IllegalArgumentException("Token expired"));

        // Act
        securityFilter.doFilterInternal(request, response, filterChain);

        // Assert
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("Token inválido"));
    }
}
