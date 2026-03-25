package com.amts.roles

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public abstract class User {
       
    private UUID id;
    private String email;
    private String hashedPassword;
    private LocalDateTime createdAt;
    private Set<Role> roles;

    public User(
        UUID id,
        String email,
        String hashedPassword,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.createdAt = createdAt;
        this.roles = EnumSet.noneOf(Role.class);
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public boolean hasRole(Role role) {
        return this.roles.contains(role);
    }

    // getters
    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public String getHashedPassword() { return hashedPassword; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public Set<Role> getRoles() { return Set.copyOf(roles); }

    // setters
    public void setEmail(String email) { this.email = email; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }
}
