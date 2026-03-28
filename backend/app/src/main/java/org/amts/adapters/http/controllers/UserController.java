package org.amts.adapters.http.controllers;

import org.amts.adapters.http.dto.UserDto;
import org.amts.application.usecases.user.UserUseCases;
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

    private final UserUseCases userUseCases;

    public UserController(UserUseCases userUseCases) {
        this.userUseCases = userUseCases;
    }

    @GetMapping
    public ResponseEntity<UserDto> getUser(
            @RequestParam(required = false) UUID id,
            @RequestParam(required = false) String email) {

        Optional<org.amts.domain.entities.user.User> user = Optional.empty();

        if (id != null) {
            user = userUseCases.getUserById(id);
        } else if (email != null) {
            user = userUseCases.getUserByEmail(email);
        } else {
            return ResponseEntity.badRequest().build();
        }

        return user.map(u -> ResponseEntity.ok(UserDto.from(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
