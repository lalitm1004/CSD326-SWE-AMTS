package org.amts.adapters.http.controllers;

import java.util.Set;
import java.util.UUID;

import org.amts.application.usecases.user.UserUseCases;
import org.amts.domain.entities.user.Role;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

record AddRolesRequest(
        UUID actorUserId,
        UUID targetUserId,
        Set<Role> roles) {
}

record RemoveRolesRequest(
        UUID actorUserId,
        UUID targetUserId,
        Set<Role> roles) {
}

@RestController
@RequestMapping("/api/user/roles")
public class RolesController {
    private final UserUseCases userUseCases;

    public RolesController(UserUseCases userUseCases) {
        this.userUseCases = userUseCases;
    }

    @PostMapping
    public ResponseEntity<Void> addRole(
            @RequestBody AddRolesRequest request) {
        userUseCases.addRoles(request.actorUserId(), request.targetUserId(), request.roles());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeRoles(
            @RequestBody RemoveRolesRequest request) {
        userUseCases.removeRoles(request.actorUserId(), request.targetUserId(), request.roles());
        return ResponseEntity.ok().build();
    }
}
