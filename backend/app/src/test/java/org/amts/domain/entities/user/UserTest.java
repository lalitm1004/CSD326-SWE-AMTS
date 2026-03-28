package org.amts.domain.entities.user;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Entity Domain Tests")
class UserTest {

    @Nested
    @DisplayName("Constructor and Validation")
    class ConstructionTests {

        @Test
        @DisplayName("userCreation - Throws Exception if role set is empty")
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
        @DisplayName("constructor - Defensively copies the roles set to prevent external mutation")
        void constructor_defensivelyCopiesRoles() {
            Set<Role> roles = new HashSet<>();
            roles.add(Role.SPECTATOR);
            
            User user = new User(UUID.randomUUID(), "test@example.com", roles, LocalDateTime.now());
            
            // Mutate the original set that was passed to the constructor
            roles.add(Role.PRESIDENT);
            
            assertFalse(user.hasRole(Role.PRESIDENT), "User roles should not be affected by modifying the original set");
            assertEquals(1, user.getRoles().size());
        }
    }

    @Nested
    @DisplayName("Role Management")
    class RoleTests {

        @Test
        @DisplayName("getRoles - Returns an unmodifiable set to preserve immutability")
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
        @DisplayName("hasRole - Correctly identifies if a user has a specific role")
        void hasRole_returnsCorrectBoolean() {
            User user = new User(UUID.randomUUID(), "test@example.com", EnumSet.of(Role.SHOW_MANAGER, Role.SALES_AGENT), LocalDateTime.now());

            assertTrue(user.hasRole(Role.SHOW_MANAGER));
            assertTrue(user.hasRole(Role.SALES_AGENT));
            assertFalse(user.hasRole(Role.SPECTATOR));
        }

        @Test
        @DisplayName("hasRolesAll - Returns true only if ALL specified roles are present")
        void hasRolesAll_validatesAllRolesPresent() {
            User user = new User(UUID.randomUUID(), "test@example.com", EnumSet.of(Role.SHOW_MANAGER, Role.SALES_AGENT), LocalDateTime.now());

            assertTrue(user.hasRolesAll(Role.SHOW_MANAGER, Role.SALES_AGENT));
            assertFalse(user.hasRolesAll(Role.SHOW_MANAGER, Role.PRESIDENT));
        }

        @Test
        @DisplayName("hasRolesAny - Returns true if AT LEAST ONE of the specified roles is present")
        void hasRolesAny_validatesAtLeastOneRolePresent() {
            User user = new User(UUID.randomUUID(), "test@example.com", EnumSet.of(Role.SHOW_MANAGER, Role.SALES_AGENT), LocalDateTime.now());

            assertTrue(user.hasRolesAny(Role.PRESIDENT, Role.SALES_AGENT));
            assertFalse(user.hasRolesAny(Role.PRESIDENT, Role.FINANCIAL_CLERK));
        }
    }

    @Nested
    @DisplayName("Equality and Identity")
    class IdentityTests {

        @Test
        @DisplayName("equals/hashCode - Based solely on UUID for entity identity")
        void equalsAndHashCode_areBasedOnlyOnId() {
            UUID id = UUID.randomUUID();
            User user1 = new User(id, "foo@example.com", EnumSet.of(Role.SPECTATOR), LocalDateTime.now());
            User user2 = new User(id, "bar@example.com", EnumSet.of(Role.ROOT), LocalDateTime.now().minusDays(1));

            assertEquals(user1, user2, "Users with same ID should be equal");
            assertEquals(user1.hashCode(), user2.hashCode(), "Users with same ID should have same hashCode");
        }
    }
}
