package org.amts.application.exceptions.user.role;

import java.util.Set;
import java.util.UUID;

import org.amts.application.exceptions.PermissionException;
import org.amts.domain.entities.user.Role;

public class IllegalRoleAssignmentException extends PermissionException {
    private final UUID actorId;
    private final Set<Role> attemptedRoles;

    public IllegalRoleAssignmentException(UUID actorId, Set<Role> attemptedRoles) {
        super("Requested role assignment is not allowed");
        this.actorId = actorId;
        this.attemptedRoles = attemptedRoles;
    }

    public UUID getActorId() {
        return actorId;
    }

    public Set<Role> getAttemptedRoles() {
        return attemptedRoles;
    }
}
