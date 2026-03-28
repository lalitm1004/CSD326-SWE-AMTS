package org.amts.application.exceptions.user;

import org.amts.application.exceptions.PersistenceException;

import java.util.UUID;

public class UserNotFoundException extends PersistenceException {
    public UserNotFoundException(UUID userId) {
        super("User not found with id:" + userId);
    }
}
