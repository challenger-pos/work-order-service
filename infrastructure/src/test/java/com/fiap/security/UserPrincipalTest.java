package com.fiap.security;

import com.fiap.core.domain.user.UserRole;
import com.fiap.persistence.entity.user.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserPrincipalTest {

    @Test
    void shouldCreateUserPrincipalWithUserEntity() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(UUID.randomUUID());
        userEntity.setEmail("test@example.com");

        // Act
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Assert
        assertNotNull(principal);
        assertEquals(userEntity, principal.getUserEntity());
    }

    @Test
    void shouldReturnCorrectUsername() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("admin@example.com");
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        String username = principal.getUsername();

        // Assert
        assertEquals("admin@example.com", username);
    }

    @Test
    void shouldReturnPasswordHash() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setPasswordHash("hashed_password_123");
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        String password = principal.getPassword();

        // Assert
        assertEquals("hashed_password_123", password);
    }

    @Test
    void shouldReturnAuthoritiesWithAdminRole() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.ADMIN);
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    @Test
    void shouldReturnAuthoritiesWithMechanicRole() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.MECHANIC);
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        // Assert
        assertNotNull(authorities);
        assertEquals(1, authorities.size());
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_MECHANIC")));
    }

    @Test
    void shouldReturnAuthoritiesWithClientRole() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.CLIENT);
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        // Assert
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_CLIENT")));
    }

    @Test
    void shouldReturnAuthoritiesWithAttendantRole() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.ATTENDANT);
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        // Assert
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_ATTENDANT")));
    }

    @Test
    void shouldReturnAuthoritiesWithMasterRole() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setRole(UserRole.MASTER);
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        Collection<? extends GrantedAuthority> authorities = principal.getAuthorities();

        // Assert
        assertTrue(authorities.contains(new SimpleGrantedAuthority("ROLE_MASTER")));
    }

    @Test
    void shouldReturnTrueForAccountNonExpired() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        boolean isAccountNonExpired = principal.isAccountNonExpired();

        // Assert
        assertTrue(isAccountNonExpired);
    }

    @Test
    void shouldReturnTrueForAccountNonLocked() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        boolean isAccountNonLocked = principal.isAccountNonLocked();

        // Assert
        assertTrue(isAccountNonLocked);
    }

    @Test
    void shouldReturnTrueForCredentialsNonExpired() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        boolean isCredentialsNonExpired = principal.isCredentialsNonExpired();

        // Assert
        assertTrue(isCredentialsNonExpired);
    }

    @Test
    void shouldReturnTrueForIsEnabled() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        UserPrincipal principal = new UserPrincipal(userEntity);

        // Act
        boolean isEnabled = principal.isEnabled();

        // Assert
        assertTrue(isEnabled);
    }

    @Test
    void shouldCreateMultiplePrincipalsWithDifferentUsers() {
        // Arrange
        UserEntity user1 = new UserEntity();
        user1.setEmail("user1@example.com");
        user1.setRole(UserRole.ADMIN);

        UserEntity user2 = new UserEntity();
        user2.setEmail("user2@example.com");
        user2.setRole(UserRole.CLIENT);

        // Act
        UserPrincipal principal1 = new UserPrincipal(user1);
        UserPrincipal principal2 = new UserPrincipal(user2);

        // Assert
        assertEquals("user1@example.com", principal1.getUsername());
        assertEquals("user2@example.com", principal2.getUsername());
        assertTrue(principal1.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN")));
        assertTrue(principal2.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_CLIENT")));
    }
}
