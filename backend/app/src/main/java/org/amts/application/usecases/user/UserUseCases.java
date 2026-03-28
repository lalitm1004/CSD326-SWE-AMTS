package org.amts.application.usecases.user;

import java.util.Optional;
import java.util.UUID;

import org.amts.domain.entities.user.User;

public class UserUseCases {
    private final UserPersistence persistence;

    public UserUseCases(UserPersistence persistence) {
        this.persistence = persistence;
    }

    public Optional<User> getUserById(UUID id) {
        return persistence.getUserById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return persistence.getUserByEmail(email);
    }
}
