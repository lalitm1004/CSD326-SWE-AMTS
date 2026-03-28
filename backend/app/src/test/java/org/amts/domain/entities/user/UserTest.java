package org.amts.domain.entities.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void userCreation_withEmptyRoles_throwsException() {
        UUID id = UUID.randomUUID();
        String email = "test@example.com";
        Set<Role> roles = EnumSet.noneOf(Role.class);
        LocalDateTime now = LocalDateTime.now();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new User(id, email, roles, now)
        );
        assertEquals("User must have at least one role", exception.getMessage());
    }

    @Test
    void getRoles_returnsUnmodifiableSet() {
        Set<Role> roles = EnumSet.of(Role.SPECTATOR);
        User user = new User(UUID.randomUUID(), "test@example.com", roles, LocalDateTime.now());

        Set<Role> retrievedRoles = user.getRoles();
        
        assertThrows(
                UnsupportedOperationException.class,
                () -> retrievedRoles.add(Role.PRESIDENT),
                "Expected getRoles() to return an unmodifiable set"
        );
    }

    @Test
    void constructor_defensivelyCopiesRoles() {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.SPECTATOR);
        
        User user = new User(UUID.randomUUID(), "test@example.com", roles, LocalDateTime.now());
        
        // Mutate the original set
        roles.add(Role.PRESIDENT);
        
        assertFalse(user.hasRole(Role.PRESIDENT), "User roles should not be affected by modifying the original set");
        assertEquals(1, user.getRoles().size());
    }

    @Test
    void hasRole_returnsCorrectBoolean() {
        User user = new User(UUID.randomUUID(), "test@example.com", EnumSet.of(Role.SHOW_MANAGER, Role.SALES_AGENT), LocalDateTime.now());

        assertTrue(user.hasRole(Role.SHOW_MANAGER));
        assertTrue(user.hasRole(Role.SALES_AGENT));
        assertFalse(user.hasRole(Role.SPECTATOR));
    }

    @Test
    void hasRolesAll_validatesAllRolesPresent() {
        User user = new User(UUID.randomUUID(), "test@example.com", EnumSet.of(Role.SHOW_MANAGER, Role.SALES_AGENT), LocalDateTime.now());

        assertTrue(user.hasRolesAll(Role.SHOW_MANAGER, Role.SALES_AGENT));
        assertFalse(user.hasRolesAll(Role.SHOW_MANAGER, Role.PRESIDENT));
    }

    @Test
    void hasRolesAny_validatesAtLeastOneRolePresent() {
        User user = new User(UUID.randomUUID(), "test@example.com", EnumSet.of(Role.SHOW_MANAGER, Role.SALES_AGENT), LocalDateTime.now());

        assertTrue(user.hasRolesAny(Role.PRESIDENT, Role.SALES_AGENT));
        assertFalse(user.hasRolesAny(Role.PRESIDENT, Role.FINANCIAL_CLERK));
    }

    @Test
    void equalsAndHashCode_areBasedOnlyOnId() {
        UUID id = UUID.randomUUID();
        User user1 = new User(id, "foo@example.com", EnumSet.of(Role.SPECTATOR), LocalDateTime.now());
        User user2 = new User(id, "bar@example.com", EnumSet.of(Role.ROOT), LocalDateTime.now().minusDays(1));

        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
