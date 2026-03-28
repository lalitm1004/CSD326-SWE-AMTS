package org.amts.application.usecases.user;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("User Use Cases Application Layer Tests")
class UserUseCasesTest {

    @Mock
    private UserPersistenceUseCase persistence;

    @Mock
    private UserRoleManagementUseCase roleManagement;

    @InjectMocks
    private UserUseCases userUseCases;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Query Operations (Lookups)")
    class QueryTests {

        @Test
        @DisplayName("getUserById - Delegates to persistence layer correctly")
        void getUserById_DelegatesToPersistence() {
            // Arrange
            UUID userId = UUID.randomUUID();
            User testUser = new User(userId, "test@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
            when(persistence.getUserById(userId)).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userUseCases.getUserById(userId);

            // Assert
            assertEquals(Optional.of(testUser), result);
            verify(persistence).getUserById(userId);
        }

        @Test
        @DisplayName("getUserByEmail - Delegates to persistence layer correctly")
        void getUserByEmail_DelegatesToPersistence() {
            // Arrange
            String email = "test@test.com";
            User testUser = new User(UUID.randomUUID(), email, Set.of(Role.SPECTATOR), LocalDateTime.now());
            when(persistence.getUserByEmail(email)).thenReturn(Optional.of(testUser));

            // Act
            Optional<User> result = userUseCases.getUserByEmail(email);

            // Assert
            assertEquals(Optional.of(testUser), result);
            verify(persistence).getUserByEmail(email);
        }
    }

    @Nested
    @DisplayName("Command Operations (Mutations)")
    class CommandTests {

        @Test
        @DisplayName("addRoles - Delegates to role management use case")
        void addRoles_DelegatesToManagement() {
            // Arrange
            UUID actorId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            Set<Role> roles = Set.of(Role.SHOW_MANAGER);

            // Act
            userUseCases.addRoles(actorId, targetId, roles);

            // Assert
            verify(roleManagement).addRoles(actorId, targetId, roles);
        }

        @Test
        @DisplayName("removeRoles - Delegates to role management use case")
        void removeRoles_DelegatesToManagement() {
            // Arrange
            UUID actorId = UUID.randomUUID();
            UUID targetId = UUID.randomUUID();
            Set<Role> roles = Set.of(Role.SHOW_MANAGER);

            // Act
            userUseCases.removeRoles(actorId, targetId, roles);

            // Assert
            verify(roleManagement).removeRoles(actorId, targetId, roles);
        }
    }
}
