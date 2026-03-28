package org.amts.application.usecases.user;

import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.user.User;

public interface UserPersistence {
    Optional<User> getUserById(UUID id);

    Optional<User> getUserByEmail(String email);
}
