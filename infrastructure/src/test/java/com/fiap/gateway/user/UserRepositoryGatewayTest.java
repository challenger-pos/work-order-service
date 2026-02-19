package com.fiap.gateway.user;

import com.fiap.core.domain.user.User;
import com.fiap.core.exception.NotFoundException;
import com.fiap.mapper.user.UserMapper;
import com.fiap.persistence.entity.user.UserEntity;
import com.fiap.persistence.repository.user.UserEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryGatewayTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserRepositoryGateway userRepositoryGateway;

    @Test
    void shouldCreateUserSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        User result = userRepositoryGateway.create(user);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        verify(userMapper).toEntity(user);
        verify(userEntityRepository).save(userEntity);
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        User result = userRepositoryGateway.update(user);

        // Assert
        assertNotNull(result);
        verify(userEntityRepository).save(userEntity);
    }

    @Test
    void shouldFindUserByIdSuccessfully() throws NotFoundException {
        // Arrange
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(userId);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        // Act
        Optional<User> result = userRepositoryGateway.findById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserDoesNotExist() {
        // Arrange
        UUID userId = UUID.randomUUID();
        when(userEntityRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            userRepositoryGateway.findById(userId);
        });
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        // Arrange
        User user = new User();
        user.setId(UUID.randomUUID());

        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());

        when(userMapper.toEntity(user)).thenReturn(userEntity);
        doNothing().when(userEntityRepository).delete(userEntity);

        // Act
        userRepositoryGateway.delete(user);

        // Assert
        verify(userEntityRepository).delete(userEntity);
    }
}
