package org.amts.adapters.usecases.user;

import org.amts.application.exceptions.user.UserNotFoundException;
import org.amts.application.usecases.user.UserPersistenceUseCase;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.amts.jooq.enums.Roleenum;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.amts.jooq.Tables.USER_PROFILE;
import static org.amts.jooq.Tables.USER_ROLE_ASSIGNMENT;

public class UserPersistenceImpl implements UserPersistenceUseCase {

    private final DSLContext dsl;

    public UserPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<User> getUserById(UUID userId) {
        var result = dsl.select(
                USER_PROFILE.ID,
                USER_PROFILE.EMAIL,
                USER_PROFILE.CREATED_AT,
                USER_ROLE_ASSIGNMENT.ROLE)
                .from(USER_PROFILE)
                .leftJoin(USER_ROLE_ASSIGNMENT).on(USER_PROFILE.ID.eq(USER_ROLE_ASSIGNMENT.USER_ID))
                .where(USER_PROFILE.ID.eq(userId))
                .fetch();

        return mapToUser(result);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        var result = dsl.select(
                USER_PROFILE.ID,
                USER_PROFILE.EMAIL,
                USER_PROFILE.CREATED_AT,
                USER_ROLE_ASSIGNMENT.ROLE)
                .from(USER_PROFILE)
                .leftJoin(USER_ROLE_ASSIGNMENT).on(USER_PROFILE.ID.eq(USER_ROLE_ASSIGNMENT.USER_ID))
                .where(USER_PROFILE.EMAIL.eq(email))
                .fetch();

        return mapToUser(result);
    }

    @Override
    public void updateUserEmail(UUID id, String newEmail) throws UserNotFoundException {
        int updated = dsl.update(USER_PROFILE)
                .set(USER_PROFILE.EMAIL, newEmail)
                .where(USER_PROFILE.ID.eq(id))
                .execute();

        if (updated == 0) {
            throw new UserNotFoundException(id);
        }
    }

    @Override
    public void updateUserRoles(UUID userId, Set<Role> newRoles) throws UserNotFoundException {
        dsl.transaction(configuration -> {
            var ctx = configuration.dsl();

            boolean exists = ctx.fetchExists(
                    ctx.selectOne()
                            .from(USER_PROFILE)
                            .where(USER_PROFILE.ID.eq(userId)));

            if (!exists) {
                throw new UserNotFoundException(userId);
            }

            Set<Role> roles = (newRoles == null || newRoles.isEmpty())
                    ? Set.of(Role.SPECTATOR)
                    : Set.copyOf(newRoles);

            // Delete existing roles
            ctx.deleteFrom(USER_ROLE_ASSIGNMENT)
                    .where(USER_ROLE_ASSIGNMENT.USER_ID.eq(userId))
                    .execute();

            // Insert new roles
            var queries = roles.stream()
                    .map((Role role) -> (org.jooq.Query) ctx.insertInto(USER_ROLE_ASSIGNMENT)
                            .set(USER_ROLE_ASSIGNMENT.USER_ID, userId)
                            .set(USER_ROLE_ASSIGNMENT.ROLE,
                                    Roleenum.valueOf(role.name())))
                    .toArray(org.jooq.Query[]::new);

            ctx.batch(queries).execute();
        });
    }

    private Optional<User> mapToUser(Result<? extends Record> result) {
        if (result.isEmpty()) {
            return Optional.empty();
        }

        var profile = result.get(0);

        Set<Role> roles = result.stream()
                .filter(r -> r.get(USER_ROLE_ASSIGNMENT.ROLE) != null)
                .map(r -> Role.valueOf(r.get(USER_ROLE_ASSIGNMENT.ROLE).name()))
                .collect(Collectors.toSet());

        User user = new User(
                profile.get(USER_PROFILE.ID),
                profile.get(USER_PROFILE.EMAIL),
                roles.isEmpty() ? Set.of(Role.SPECTATOR) : roles, // Ensure atleast one role
                profile.get(USER_PROFILE.CREATED_AT));

        return Optional.of(user);
    }
}
