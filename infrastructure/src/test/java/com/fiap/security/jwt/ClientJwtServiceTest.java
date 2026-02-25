package com.fiap.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ClientJwtServiceTest {

    private ClientJwtService clientJwtService;
    private String secret;
    private Key key;

    @BeforeEach
    void setUp() {
        clientJwtService = new ClientJwtService();
        key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        secret = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(clientJwtService, "secret", secret);
    }

    @Test
    void shouldExtractDocumentNumberFromToken() {
        // Arrange
        String documentNumber = "12345678901";
        String token = createToken(documentNumber);

        // Act
        String result = clientJwtService.getDocumentFromToken(token);

        // Assert
        assertEquals(documentNumber, result);
    }

    @Test
    void shouldExtractDifferentDocumentNumbers() {
        // Arrange
        String doc1 = "11111111111";
        String doc2 = "99999999999";
        String token1 = createToken(doc1);
        String token2 = createToken(doc2);

        // Act
        String result1 = clientJwtService.getDocumentFromToken(token1);
        String result2 = clientJwtService.getDocumentFromToken(token2);

        // Assert
        assertEquals(doc1, result1);
        assertEquals(doc2, result2);
        assertNotEquals(result1, result2);
    }

    @Test
    void shouldHandleInvalidTokenFormat() {
        // Arrange
        String invalidToken = "invalid.token.format";

        // Act & Assert
        assertThrows(Exception.class, () -> clientJwtService.getDocumentFromToken(invalidToken));
    }

    @Test
    void shouldHandleExpiredToken() {
        // Arrange
        String documentNumber = "12345678901";
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String expiredToken = Jwts.builder()
                .claim("documentNumber", documentNumber)
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key)
                .compact();

        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(clientJwtService, "secret", secret);

        // Act & Assert
        assertThrows(Exception.class, () -> clientJwtService.getDocumentFromToken(expiredToken));
    }

    @Test
    void shouldHandleMissingDocumentNumberClaim() {
        // Arrange
        String token = Jwts.builder()
                .claim("otherClaim", "value")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        // Act
        String result = clientJwtService.getDocumentFromToken(token);

        // Assert
        assertNull(result);
    }

    @Test
    void shouldHandleTokenSignedWithDifferentKey() {
        // Arrange
        String documentNumber = "12345678901";
        Key differentKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = Jwts.builder()
                .claim("documentNumber", documentNumber)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(differentKey)
                .compact();

        // Act & Assert
        assertThrows(Exception.class, () -> clientJwtService.getDocumentFromToken(token));
    }

    @Test
    void shouldHandleValidTokenWithExpirationInFuture() {
        // Arrange
        String documentNumber = "55555555555";
        String token = Jwts.builder()
                .claim("documentNumber", documentNumber)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(clientJwtService, "secret", secret);

        // Act
        String result = clientJwtService.getDocumentFromToken(token);

        // Assert
        assertEquals(documentNumber, result);
    }

    @Test
    void shouldHandleEmptyDocumentNumber() {
        // Arrange
        String documentNumber = "";
        String token = createToken(documentNumber);

        // Act
        String result = clientJwtService.getDocumentFromToken(token);

        // Assert
        assertEquals("", result);
    }

    @Test
    void shouldHandleMultipleClaims() {
        // Arrange
        String documentNumber = "12345678901";
        String token = Jwts.builder()
                .claim("documentNumber", documentNumber)
                .claim("role", "ADMIN")
                .claim("email", "user@example.com")
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();

        String secret = Base64.getEncoder().encodeToString(key.getEncoded());
        ReflectionTestUtils.setField(clientJwtService, "secret", secret);

        // Act
        String result = clientJwtService.getDocumentFromToken(token);

        // Assert
        assertEquals(documentNumber, result);
    }

    // Helper method to create a valid token
    private String createToken(String documentNumber) {
        return Jwts.builder()
                .claim("documentNumber", documentNumber)
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
}
