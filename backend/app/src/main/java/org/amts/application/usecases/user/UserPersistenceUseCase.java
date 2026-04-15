package org.amts.application.usecases.user;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.List;

import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

public interface UserPersistenceUseCase {
    Optional<User> getUserById(UUID userId);

    Optional<User> getUserByEmail(String email);

    void updateUserEmail(UUID userId, String newEmail);

    void updateUserRoles(UUID userId, Set<Role> newRoles);

    List<User> getAllUsers();

    java.util.List<UUID> getAllFinancialClerks();

    java.util.List<UUID> getAllSalesAgents();
}
