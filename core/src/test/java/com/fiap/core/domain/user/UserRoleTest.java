package com.fiap.core.domain.user;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserRoleTest {

    @Test
    void shouldHaveAllUserRoles() {
        // Assert
        assertNotNull(UserRole.MASTER);
        assertNotNull(UserRole.ADMIN);
        assertNotNull(UserRole.MECHANIC);
        assertNotNull(UserRole.ATTENDANT);
        assertNotNull(UserRole.CLIENT);
    }

    @Test
    void shouldConvertUserRoleToString() {
        // Assert
        assertEquals("MASTER", UserRole.MASTER.toString());
        assertEquals("ADMIN", UserRole.ADMIN.toString());
        assertEquals("MECHANIC", UserRole.MECHANIC.toString());
        assertEquals("ATTENDANT", UserRole.ATTENDANT.toString());
        assertEquals("CLIENT", UserRole.CLIENT.toString());
    }

    @Test
    void shouldConvertStringToUserRole() {
        // Arrange & Act & Assert
        assertEquals(UserRole.MASTER, UserRole.valueOf("MASTER"));
        assertEquals(UserRole.ADMIN, UserRole.valueOf("ADMIN"));
        assertEquals(UserRole.MECHANIC, UserRole.valueOf("MECHANIC"));
        assertEquals(UserRole.ATTENDANT, UserRole.valueOf("ATTENDANT"));
        assertEquals(UserRole.CLIENT, UserRole.valueOf("CLIENT"));
    }

    @Test
    void shouldThrowExceptionForInvalidRole() {
        // Assert
        assertThrows(IllegalArgumentException.class, () -> {
            UserRole.valueOf("INVALID_ROLE");
        });
    }

    @Test
    void shouldCompareUserRoles() {
        // Assert
        assertEquals(UserRole.ADMIN, UserRole.ADMIN);
        assertNotEquals(UserRole.ADMIN, UserRole.MECHANIC);
        assertTrue(UserRole.MASTER.equals(UserRole.MASTER));
    }

    @Test
    void shouldGetUserRoleOrdinal() {
        // Assert
        assertEquals(0, UserRole.MASTER.ordinal());
        assertEquals(1, UserRole.ADMIN.ordinal());
        assertEquals(2, UserRole.MECHANIC.ordinal());
        assertEquals(3, UserRole.ATTENDANT.ordinal());
        assertEquals(4, UserRole.CLIENT.ordinal());
    }

    @Test
    void shouldIterateAllUserRoles() {
        // Assert
        UserRole[] roles = UserRole.values();
        assertEquals(5, roles.length);
        assertTrue(containsRole(roles, UserRole.MASTER));
        assertTrue(containsRole(roles, UserRole.CLIENT));
    }

    private boolean containsRole(UserRole[] roles, UserRole targetRole) {
        for (UserRole role : roles) {
            if (role.equals(targetRole)) {
                return true;
            }
        }
        return false;
    }
}
