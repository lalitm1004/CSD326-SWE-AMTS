package org.amts.adapters.usecases;

import java.util.UUID;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.application.exceptions.user.UserNotFoundException;

public class AuthorizationHelper {
    public static User getAuthorizedUser(UUID userId, UserPersistenceUseCase userPersistence) {
        User user = userPersistence.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (!user.hasRolesAny(Role.AUDITORIUM_SECRETARY, Role.SHOW_MANAGER)) {
            throw new RuntimeException("Unauthorized");
        }

        return user;
    }
}
