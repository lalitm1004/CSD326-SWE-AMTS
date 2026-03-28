package org.amts.application.exceptions.user.role;

import java.util.Set;
import java.util.UUID;

import org.amts.application.exceptions.PermissionException;
import org.amts.domain.entities.user.Role;

public class UnauthorizedRoleAssignmentException extends PermissionException {
    private final UUID actorId;
    private final Set<Role> roles;

    public UnauthorizedRoleAssignmentException(UUID actorId, Set<Role> roles) {
        super("User is not allowed to assign roles");
        this.actorId = actorId;
        this.roles = roles;
    }

    public UUID getActorId() {
        return actorId;
    }

    public Set<Role> getRoles() {
        return roles;
    }
}