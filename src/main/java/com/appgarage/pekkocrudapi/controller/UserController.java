package com.appgarage.pekkocrudapi.controller;

import com.appgarage.pekkocrudapi.actor.UserMessages;
import com.appgarage.pekkocrudapi.model.User;
import com.appgarage.pekkocrudapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;

/**
 * REST controller for User CRUD operations and retrieving all users.
 * Handles HTTP requests and delegates to UserService.
 */
@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<?>> createUser(@Valid @RequestBody User user) {
        return userService.createUser(user)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response.getError());
                    }
                    return ResponseEntity.status(HttpStatus.CREATED).body(response.getUser());
                });
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<?>> getUser(@PathVariable String id) {
        return userService.getUser(id)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getError());
                    }
                    return ResponseEntity.ok(response.getUser());
                });
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<?>> getAllUsers() {
        return userService.getAllUsers()
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response.getError());
                    }
                    return ResponseEntity.ok(response.getUsers());
                });
    }

    @PutMapping("/{id}")
    public CompletableFuture<ResponseEntity<?>> updateUser(@PathVariable String id, @Valid @RequestBody User user) {
        return userService.updateUser(id, user)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getError());
                    }
                    return ResponseEntity.ok(response.getUser());
                });
    }

    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<?>> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id)
                .thenApply(response -> {
                    if (response.getError() != null) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response.getError());
                    }
                    return ResponseEntity.noContent().build();
                });
    }
}
