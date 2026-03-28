package org.amts.adapters.http.dto;

import org.amts.domain.entities.user.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class UserDto {
    private final UUID id;
    private final String email;
    private final Set<String> roles;
    private final LocalDateTime createdAt;

    public UserDto(UUID id, String email, Set<String> roles, LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.createdAt = createdAt;
    }

    public static UserDto from(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(java.lang.Enum::name)
                .collect(Collectors.toSet());
        return new UserDto(user.getId(), user.getEmail(), roles, user.getCreatedAt());
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
