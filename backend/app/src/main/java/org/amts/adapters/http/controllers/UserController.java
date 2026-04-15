package org.amts.adapters.http.controllers;

import org.amts.application.usecases.user.UserUseCases;
import org.amts.domain.entities.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    record UserIdsResponse(
            java.util.List<UUID> userIds
    ) {}

    private final UserUseCases userUseCases;

    public UserController(UserUseCases userUseCases) {
        this.userUseCases = userUseCases;
    }

    @GetMapping
    public ResponseEntity<User> getUser(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String email) {

        Optional<User> user = Optional.empty();

        if (id != null) {
            user = userUseCases.getUserById(id);
        } else if (email != null) {
            user = userUseCases.getUserByEmail(email);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return user.map(u -> ResponseEntity.ok(u))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/all")
    public ResponseEntity<java.util.List<User>> getAllUsers(
            @RequestParam UUID actorUserId) {
        return ResponseEntity.ok(userUseCases.getAllUsers(actorUserId));
    }

    @GetMapping("/clerks")
    public ResponseEntity<UserIdsResponse> getAllFinancialClerks(
            @RequestParam UUID actorUserId) {
        return ResponseEntity.ok(new UserIdsResponse(userUseCases.getAllFinancialClerks(actorUserId)));
    }

    @GetMapping("/sales-agents")
    public ResponseEntity<UserIdsResponse> getAllSalesAgents(
            @RequestParam UUID actorUserId) {
        return ResponseEntity.ok(new UserIdsResponse(userUseCases.getAllSalesAgents(actorUserId)));
    }
}
