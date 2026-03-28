package org.amts.application.usecases.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

public class UserUseCases {
    private final UserPersistenceUseCase persistence;
    private final UserRoleManagementUseCase assignUserRoles;

    public UserUseCases(UserPersistenceUseCase persistence, UserRoleManagementUseCase assignUserRoles) {
        this.persistence = persistence;
        this.assignUserRoles = assignUserRoles;
    }

    public Optional<User> getUserById(UUID userId) {
        return persistence.getUserById(userId);
    }

    public Optional<User> getUserByEmail(String email) {
        return persistence.getUserByEmail(email);
    }

    public void addRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles) {
        assignUserRoles.addRoles(actorUserId, targetUserId, roles);
    }

    public void removeRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles) {
        assignUserRoles.removeRoles(actorUserId, targetUserId, roles);
    }
}
