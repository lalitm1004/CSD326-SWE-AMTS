package org.amts.domain.entities.user;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class User {

    private final UUID id;
    private final String email;
    private final Set<Role> roles;
    private final LocalDateTime createdAt;

    public User(UUID id, String email, Set<Role> roles, LocalDateTime createdAt) {
        this.id = Objects.requireNonNull(id, "User id must not be null");
        this.email = Objects.requireNonNull(email, "User email must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "User createdAt must not be null");

        Objects.requireNonNull(roles, "User roles must not be null");
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("User must have at least one role");
        }
        this.roles = EnumSet.copyOf(roles);
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", email='" + email + "'}";
    }
}
