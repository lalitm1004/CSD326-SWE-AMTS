package org.amts.adapters.usecases.user;

import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import org.amts.application.usecases.user.UserRoleManagementUseCase;
import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.exceptions.user.role.IllegalRoleAssignmentException;
import org.amts.application.exceptions.user.role.UnauthorizedRoleAssignmentException;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;

public class UserRoleManagementImpl implements UserRoleManagementUseCase {

    private final UserPersistenceUseCase persistence;

    public UserRoleManagementImpl(UserPersistenceUseCase persistence) {
        this.persistence = persistence;
    }

    @Override
    public void addRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles) {

        User actor = getUserOrThrow(actorUserId);
        User target = getUserOrThrow(targetUserId);

        validateNotRoot(roles, actorUserId);
        validatePermissions(actor, roles, actorUserId);

        Set<Role> updatedRoles = EnumSet.copyOf(target.getRoles());
        updatedRoles.addAll(roles);

        ensureSpectator(updatedRoles);

        persistence.updateUserRoles(targetUserId, updatedRoles);
    }

    @Override
    public void removeRoles(UUID actorUserId, UUID targetUserId, Set<Role> roles) {

        User actor = getUserOrThrow(actorUserId);
        User target = getUserOrThrow(targetUserId);

        validateNotRoot(roles, actorUserId);
        validatePermissions(actor, roles, actorUserId);

        Set<Role> updatedRoles = EnumSet.copyOf(target.getRoles());
        updatedRoles.removeAll(roles);

        ensureSpectator(updatedRoles);

        persistence.updateUserRoles(targetUserId, updatedRoles);
    }

    private User getUserOrThrow(UUID userId) {
        return persistence.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
    }

    private void validateNotRoot(Set<Role> roles, UUID actorId) {

        if (roles.contains(Role.ROOT)) {
            throw new IllegalRoleAssignmentException(actorId, roles);
        }
    }

    private void validatePermissions(User actor, Set<Role> roles, UUID actorId) {

        // No relevant authority at all
        if (!actor.hasRolesAny(Role.PRESIDENT, Role.AUDITORIUM_SECRETARY)) {
            throw new UnauthorizedRoleAssignmentException(actorId, roles);
        }

        for (Role role : roles) {

            if (actor.hasRole(Role.PRESIDENT)) {

                // President limited scope
                if (!(role == Role.PRESIDENT || role == Role.AUDITORIUM_SECRETARY)) {
                    throw new IllegalRoleAssignmentException(actorId, roles);
                }

            } else if (actor.hasRole(Role.AUDITORIUM_SECRETARY)) {

                // Audi-sec restricted from high-level roles
                if (role == Role.PRESIDENT || role == Role.AUDITORIUM_SECRETARY) {
                    throw new IllegalRoleAssignmentException(actorId, roles);
                }
            }
        }
    }

    private void ensureSpectator(Set<Role> roles) {

        // Enforce invariant
        if (roles.isEmpty() || !roles.contains(Role.SPECTATOR)) {
            roles.add(Role.SPECTATOR);
        }
    }
}