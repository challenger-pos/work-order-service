package com.fiap.service;

import com.fiap.core.domain.user.User;
import com.fiap.core.exception.EmailException;
import com.fiap.core.exception.NotFoundException;
import com.fiap.core.exception.PasswordException;
import com.fiap.core.exception.enums.ErrorCodeEnum;
import com.fiap.gateway.user.UserRepositoryGateway;
import com.fiap.mapper.user.UserMapper;
import com.fiap.persistence.entity.user.UserEntity;
import com.fiap.persistence.repository.user.UserEntityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserGatewayImplTest {
    private UserEntityRepository userEntityRepository;
    private UserMapper userMapper;
    private UserRepositoryGateway userGateway;

    @BeforeEach
    void setUp() {
        userEntityRepository = mock(UserEntityRepository.class);
        userMapper = mock(UserMapper.class);
        userGateway = new UserRepositoryGateway(userEntityRepository, userMapper);
    }

    @Test
    void shouldCreateUserSuccessfully() throws EmailException, PasswordException {
        var user = new User(UUID.randomUUID(), "Test User", "teste@gmail.com", "MECHANIC", "Password@123", LocalDateTime.now(), LocalDateTime.now());

        var userEntity = UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .passwordHash(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        var result = userGateway.create(user);

        assertNotNull(result);
        verify(userMapper, times(1)).toEntity(user);
        verify(userEntityRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldThrowExceptionWhenMapperFailsOnCreate() {
        var user = new User();
        user.setId(UUID.randomUUID());

        when(userMapper.toEntity(user)).thenThrow(new RuntimeException("Mapping error"));

        assertThrows(RuntimeException.class, () -> userGateway.create(user));

        verify(userMapper, times(1)).toEntity(user);
        verify(userEntityRepository, never()).save(any());
    }

    @Test
    void shouldUpdateUserSuccessfully() throws PasswordException, EmailException {
        var user = new User(UUID.randomUUID(), "Updated User", "update@gmail.com", "ADMIN", "NewPassword@123", LocalDateTime.now(), LocalDateTime.now());

        var userEntity = UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .passwordHash(user.getPassword())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        var result = userGateway.update(user);

        assertNotNull(result);
        verify(userMapper, times(1)).toEntity(user);
        verify(userEntityRepository, times(1)).save(userEntity);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldFindUserByIdSuccessfully() throws NotFoundException {
        var userId = UUID.randomUUID();
        var userEntity = new UserEntity();
        var user = new User();
        user.setId(userId);
        userEntity.setId(userId);

        when(userEntityRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        var result = userGateway.findById(userId);

        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        verify(userEntityRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldThrowNotFoundExceptionWhenUserNotFound() throws PasswordException, EmailException {
        var userId = UUID.randomUUID();

        when(userEntityRepository.findById(userId)).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> userGateway.findById(userId));

        assertEquals(ErrorCodeEnum.USE0007.getMessage(), exception.getMessage());
        assertEquals(ErrorCodeEnum.USE0007.getCode(), exception.getCode());
        verify(userEntityRepository, times(1)).findById(userId);

        verify(userMapper, never()).toDomain(any(UserEntity.class));
    }

    @Test
    void shouldDeleteUserSuccessfully() throws PasswordException, EmailException {
        var user = new User(UUID.randomUUID(), "Delete User", "delete@gmail.com", "CLIENT", "Delete@123", LocalDateTime.now(), LocalDateTime.now());
        var userEntity = new UserEntity();
        userEntity.setId(user.getId());

        when(userMapper.toEntity(user)).thenReturn(userEntity);

        userGateway.delete(user);

        verify(userMapper, times(1)).toEntity(user);
        verify(userEntityRepository, times(1)).delete(userEntity);
    }

    @Test
    void shouldFindUserByEmailSuccessfully() throws EmailException {
        String email = "user@fiap.com";
        var userEntity = new UserEntity();
        var user = new User();
        user.setEmail(email);

        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.of(userEntity));
        when(userMapper.toDomain(userEntity)).thenReturn(user);

        var result = userGateway.findByEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
        verify(userEntityRepository, times(1)).findByEmail(email);
        verify(userMapper, times(1)).toDomain(userEntity);
    }

    @Test
    void shouldReturnEmptyWhenEmailNotFound() throws PasswordException, EmailException {
        String email = "notfound@fiap.com";

        when(userEntityRepository.findByEmail(email)).thenReturn(Optional.empty());

        var result = userGateway.findByEmail(email);

        assertTrue(result.isEmpty());
        verify(userEntityRepository, times(1)).findByEmail(email);

        verify(userMapper, never()).toDomain(any(UserEntity.class));
    }

    @Test
    void shouldThrowExceptionWhenRepositoryFailsOnSave() throws PasswordException, EmailException {
        var user = new User(UUID.randomUUID(), "Test", "test@gmail.com", "MECHANIC", "Pass@123", LocalDateTime.now(), LocalDateTime.now());
        var userEntity = new UserEntity();

        when(userMapper.toEntity(user)).thenReturn(userEntity);
        when(userEntityRepository.save(userEntity)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> userGateway.create(user));

        verify(userMapper, times(1)).toEntity(user);
        verify(userEntityRepository, times(1)).save(userEntity);
        verify(userMapper, never()).toDomain(any(UserEntity.class));
    }
}