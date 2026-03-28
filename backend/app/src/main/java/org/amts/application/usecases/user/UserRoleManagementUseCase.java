package org.amts.application.usecases.user;

import java.util.Set;
import java.util.UUID;

import org.amts.domain.entities.user.Role;

public interface UserRoleManagementUseCase {
    void addRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles);

    void removeRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles);
}