package org.amts.adapters.persistence;

import org.amts.application.usecases.user.UserPersistence;
import org.amts.domain.entities.user.Role;
import org.amts.domain.entities.user.User;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.Result;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.amts.jooq.Tables.USER_PROFILE;
import static org.amts.jooq.Tables.USER_ROLE_ASSIGNMENT;

public class UserPersistenceImpl implements UserPersistence {

    private final DSLContext dsl;

    public UserPersistenceImpl(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    public Optional<User> getUserById(UUID id) {
        var result = dsl.select(
                USER_PROFILE.ID,
                USER_PROFILE.EMAIL,
                USER_PROFILE.CREATED_AT,
                USER_ROLE_ASSIGNMENT.ROLE)
                .from(USER_PROFILE)
                .leftJoin(USER_ROLE_ASSIGNMENT).on(USER_PROFILE.ID.eq(USER_ROLE_ASSIGNMENT.USER_ID))
                .where(USER_PROFILE.ID.eq(id))
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
