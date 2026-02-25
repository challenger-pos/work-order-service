package com.fiap.mapper.user;

import com.fiap.core.domain.user.User;
import com.fiap.core.domain.user.UserRole;
import com.fiap.core.exception.EmailException;
import com.fiap.core.exception.PasswordException;
import com.fiap.dto.user.CreateUserRequest;
import com.fiap.dto.user.UpdateUserRequest;
import com.fiap.dto.user.UserResponse;
import com.fiap.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    @InjectMocks
    private UserMapper userMapper;

    @Test
    void shouldMapCreateUserRequestToDomain() throws EmailException, PasswordException {
        // Arrange
        CreateUserRequest request = new CreateUserRequest(
                "João Silva",
                "joao@example.com",
                "MECHANIC",
                "Senha@123"
        );

        // Act
        User result = userMapper.toDomain(request);

        // Assert
        assertNotNull(result);
        assertEquals("João Silva", result.getName());
        assertEquals("joao@example.com", result.getEmail());
        assertEquals("MECHANIC", result.getRole().toString());
    }

    @Test
    void shouldMapUpdateUserRequestToDomain() throws EmailException, PasswordException {
        // Arrange
        UUID userId = UUID.randomUUID();
        UpdateUserRequest request = new UpdateUserRequest(
                userId,
                "Carlos Silva",
                "carlos@example.com",
                "ADMIN",
                "NovaSenha@123"
        );

        // Act
        User result = userMapper.toDomainUpdate(request);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Carlos Silva", result.getName());
        assertEquals("carlos@example.com", result.getEmail());
        assertEquals("ADMIN", result.getRole().toString());
    }

    @Test
    void shouldMapUserToResponse() throws EmailException, PasswordException {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(
            userId,
            "Maria",
            "maria@example.com",
            "CLIENT",
            "Senha@456"
        );

        // Act
        UserResponse result = userMapper.toResponse(user);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.id());
        assertEquals("Maria", result.name());
        assertEquals("maria@example.com", result.email());
    }

    @Test
    void shouldMapUserToEntity() throws EmailException, PasswordException {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User(
                userId,
                "Pedro",
                "pedro@example.com",
                "MECHANIC",
                "Senha@789"
        );

        // Act
        UserEntity result = userMapper.toEntity(user);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Pedro", result.getName());
        assertEquals("pedro@example.com", result.getEmail());
    }

    @Test
    void shouldMapUserEntityToDomain() {
        // Arrange
        UUID userId = UUID.randomUUID();
        UserEntity entity = new UserEntity();
        entity.setId(userId);
        entity.setName("Ana");
        entity.setEmail("ana@example.com");
        entity.setRole(com.fiap.core.domain.user.UserRole.ADMIN);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setPasswordHash("Abcdef1!");

        // Act
        User result = userMapper.toDomain(entity);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("Ana", result.getName());
        assertEquals("ana@example.com", result.getEmail());
    }

    @Test
    void shouldMapMultipleUsersToResponse() throws EmailException, PasswordException {
        // Arrange
        UUID userId1 = UUID.randomUUID();
        UUID userId2 = UUID.randomUUID();

        User user1 = new User(userId1, "User1", "user1@example.com", "CLIENT", "Pass@123");
        User user2 = new User(userId2, "User2", "user2@example.com", "MECHANIC", "Pass@456");

        // Act
        UserResponse response1 = userMapper.toResponse(user1);
        UserResponse response2 = userMapper.toResponse(user2);

        // Assert
        assertNotNull(response1);
        assertNotNull(response2);
        assertEquals("User1", response1.name());
        assertEquals("User2", response2.name());
    }
}
