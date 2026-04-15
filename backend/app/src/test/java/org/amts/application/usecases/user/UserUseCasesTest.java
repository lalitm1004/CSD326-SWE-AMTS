package org.amts.application.usecases.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.amts.application.exceptions.PermissionException;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.junit.jupiter.api.Test;

class UserUseCasesTest {

    private final UserPersistenceUseCase persistence = mock(UserPersistenceUseCase.class);
    private final UserRoleManagementUseCase roleManagement = mock(UserRoleManagementUseCase.class);
    private final UserUseCases useCases = new UserUseCases(persistence, roleManagement);

    @Test
    void getAllUsersReturnsUsersForAuthorizedActor() {
        UUID actorUserId = UUID.randomUUID();
        User actor = new User(actorUserId, "president@snu.edu.in", Set.of(Role.PRESIDENT), LocalDateTime.now());
        List<User> expectedUsers = List.of(
                actor,
                new User(UUID.randomUUID(), "spectator@snu.edu.in", Set.of(Role.SPECTATOR), LocalDateTime.now())
        );

        when(persistence.getUserById(actorUserId)).thenReturn(Optional.of(actor));
        when(persistence.getAllUsers()).thenReturn(expectedUsers);

        List<User> actualUsers = useCases.getAllUsers(actorUserId);

        assertSame(expectedUsers, actualUsers);
        verify(persistence).getAllUsers();
    }

    @Test
    void getAllUsersRejectsUnauthorizedActor() {
        UUID actorUserId = UUID.randomUUID();
        User actor = new User(actorUserId, "agent@snu.edu.in", Set.of(Role.SALES_AGENT), LocalDateTime.now());

        when(persistence.getUserById(actorUserId)).thenReturn(Optional.of(actor));

        PermissionException ex = assertThrows(PermissionException.class, () -> useCases.getAllUsers(actorUserId));

        assertEquals("You do not have permission to perform this action.", ex.getMessage());
        verify(persistence, never()).getAllUsers();
    }
}
