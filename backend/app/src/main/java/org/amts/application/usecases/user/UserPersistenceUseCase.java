package org.amts.application.usecases.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

public interface UserPersistenceUseCase {
    Optional<User> getUserById(UUID userId);

    Optional<User> getUserByEmail(String email);

    void updateUserEmail(UUID userId, String newEmail);

    void updateUserRoles(UUID userId, Set<Role> newRoles);
}
