package org.amts.adapters.usecases;

import java.util.UUID;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

import org.amts.application.exceptions.PermissionException;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.application.exceptions.user.UserNotFoundException;

public class AuthorizationHelper {

    public static User getAuthorizedUser(
            UUID userId,
            UserPersistenceUseCase userPersistence,
            Role... requiredRoles) {
        User user = userPersistence.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (requiredRoles.length > 0
                && !user.hasRolesAny(Role.ROOT)
                && !user.hasRolesAny(requiredRoles)) {
            throw new PermissionException("You do not have permission to perform this action.");
        }

        return user;
    }
}