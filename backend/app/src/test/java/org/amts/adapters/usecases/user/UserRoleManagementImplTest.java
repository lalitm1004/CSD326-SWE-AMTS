package org.amts.adapters.usecases.user;

import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.exceptions.user.role.IllegalRoleAssignmentException;
import org.amts.application.exceptions.user.role.UnauthorizedRoleAssignmentException;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserRoleManagement Implementation Tests")
class UserRoleManagementImplTest {

    @Mock
    private UserPersistenceUseCase persistence;

    @InjectMocks
    private UserRoleManagementImpl roleManagement;

    private UUID actorId;
    private UUID targetId;
    private User target;

    @BeforeEach
    void setUp() {
        actorId = UUID.randomUUID();
        targetId = UUID.randomUUID();
        // Standard target user with basic SPECTATOR role
        target = new User(targetId, "target@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
    }

    @Nested
    @DisplayName("Role Addition via addRoles()")
    class AddRolesTests {

        @Test
        @DisplayName("addRoles - Throws UserNotFoundException if actor record missing")
        void addRoles_ActorMissing_ThrowsException() {
            when(persistence.getUserById(actorId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> roleManagement.addRoles(actorId, targetId, Set.of(Role.SHOW_MANAGER)));
        }

        @Test
        @DisplayName("addRoles - Throws UserNotFoundException if target record missing")
        void addRoles_TargetMissing_ThrowsException() {
            User actor = new User(actorId, "actor@test.com", Set.of(Role.PRESIDENT), LocalDateTime.now());
            when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
            when(persistence.getUserById(targetId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> roleManagement.addRoles(actorId, targetId, Set.of(Role.AUDITORIUM_SECRETARY)));
        }

        @Test
        @DisplayName("addRoles - Throws UnauthorizedRoleAssignmentException for actors with only SPECTATOR role")
        void addRoles_SpectatorActor_ThrowsUnauthorized() {
            User actor = new User(actorId, "actor@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
            when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
            when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

            assertThrows(UnauthorizedRoleAssignmentException.class,
                    () -> roleManagement.addRoles(actorId, targetId, Set.of(Role.SHOW_MANAGER)));
        }

        @Nested
        @DisplayName("President Permissions")
        class PresidentPermissions {
            @Test
            @DisplayName("President - Can assign PRESIDENT role")
            void president_AssignsPresident_Success() {
                User actor = new User(actorId, "actor@test.com", Set.of(Role.PRESIDENT), LocalDateTime.now());
                when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
                when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

                roleManagement.addRoles(actorId, targetId, new HashSet<>(Set.of(Role.PRESIDENT)));

                Set<Role> expectedRoles = EnumSet.of(Role.SPECTATOR, Role.PRESIDENT);
                verify(persistence).updateUserRoles(eq(targetId), eq(expectedRoles));
            }

            @Test
            @DisplayName("President - Can assign AUDITORIUM_SECRETARY role")
            void president_AssignsSecretary_Success() {
                User actor = new User(actorId, "actor@test.com", Set.of(Role.PRESIDENT), LocalDateTime.now());
                when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
                when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

                roleManagement.addRoles(actorId, targetId, new HashSet<>(Set.of(Role.AUDITORIUM_SECRETARY)));

                Set<Role> expectedRoles = EnumSet.of(Role.SPECTATOR, Role.AUDITORIUM_SECRETARY);
                verify(persistence).updateUserRoles(eq(targetId), eq(expectedRoles));
            }

            @Test
            @DisplayName("President - Cannot assign SHOW_MANAGER (illegal assignment)")
            void president_AssignsManager_ThrowsIllegal() {
                User actor = new User(actorId, "actor@test.com", Set.of(Role.PRESIDENT), LocalDateTime.now());
                when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
                when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

                assertThrows(IllegalRoleAssignmentException.class,
                        () -> roleManagement.addRoles(actorId, targetId, Set.of(Role.SHOW_MANAGER)));
            }
        }

        @Nested
        @DisplayName("Secretary Permissions")
        class SecretaryPermissions {
            @Test
            @DisplayName("Secretary - Can assign SHOW_MANAGER role")
            void secretary_AssignsManager_Success() {
                User actor = new User(actorId, "actor@test.com", Set.of(Role.AUDITORIUM_SECRETARY), LocalDateTime.now());
                when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
                when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

                roleManagement.addRoles(actorId, targetId, new HashSet<>(Set.of(Role.SHOW_MANAGER)));

                Set<Role> expectedRoles = EnumSet.of(Role.SPECTATOR, Role.SHOW_MANAGER);
                verify(persistence).updateUserRoles(eq(targetId), eq(expectedRoles));
            }

            @Test
            @DisplayName("Secretary - Cannot assign PRESIDENT role")
            void secretary_AssignsPresident_ThrowsIllegal() {
                User actor = new User(actorId, "actor@test.com", Set.of(Role.AUDITORIUM_SECRETARY), LocalDateTime.now());
                when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
                when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

                assertThrows(IllegalRoleAssignmentException.class,
                        () -> roleManagement.addRoles(actorId, targetId, Set.of(Role.PRESIDENT)));
            }
        }

        @Test
        @DisplayName("addRoles - Cannot assign ROOT role (System restricted)")
        void addRoles_AssignRoot_ThrowsIllegal() {
            User actor = new User(actorId, "actor@test.com", Set.of(Role.PRESIDENT), LocalDateTime.now());
            when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
            when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

            assertThrows(IllegalRoleAssignmentException.class,
                    () -> roleManagement.addRoles(actorId, targetId, Set.of(Role.ROOT)));
        }
    }

    @Nested
    @DisplayName("Role Removal via removeRoles()")
    class RemoveRolesTests {

        @Test
        @DisplayName("removeRoles - Ensure SPECTATOR role is always preserved even if explicit removal requested")
        void removeRoles_PreservesSpectator() {
            User actor = new User(actorId, "actor@test.com", Set.of(Role.AUDITORIUM_SECRETARY), LocalDateTime.now());
            target = new User(targetId, "target@test.com", Set.of(Role.SPECTATOR, Role.SHOW_MANAGER), LocalDateTime.now());

            when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
            when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

            roleManagement.removeRoles(actorId, targetId, new HashSet<>(Set.of(Role.SPECTATOR, Role.SHOW_MANAGER)));

            // Domain rule: User should always have at least SPECTATOR
            Set<Role> expectedRoles = EnumSet.of(Role.SPECTATOR);
            verify(persistence).updateUserRoles(eq(targetId), eq(expectedRoles));
        }

        @Test
        @DisplayName("removeRoles - Throws UserNotFoundException if actor missing")
        void removeRoles_ActorMissing_ThrowsException() {
            when(persistence.getUserById(actorId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> roleManagement.removeRoles(actorId, targetId, Set.of(Role.SHOW_MANAGER)));
        }

        @Test
        @DisplayName("removeRoles - Throws UserNotFoundException if target missing")
        void removeRoles_TargetMissing_ThrowsException() {
            User actor = new User(actorId, "actor@test.com", Set.of(Role.PRESIDENT), LocalDateTime.now());
            when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
            when(persistence.getUserById(targetId)).thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> roleManagement.removeRoles(actorId, targetId, Set.of(Role.AUDITORIUM_SECRETARY)));
        }

        @Test
        @DisplayName("removeRoles - Throws UnauthorizedRoleAssignmentException for SPECTATOR actors")
        void removeRoles_SpectatorActor_ThrowsUnauthorized() {
            User actor = new User(actorId, "actor@test.com", Set.of(Role.SPECTATOR), LocalDateTime.now());
            when(persistence.getUserById(actorId)).thenReturn(Optional.of(actor));
            when(persistence.getUserById(targetId)).thenReturn(Optional.of(target));

            assertThrows(UnauthorizedRoleAssignmentException.class,
                    () -> roleManagement.removeRoles(actorId, targetId, Set.of(Role.SHOW_MANAGER)));
        }
    }
}
