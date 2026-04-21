package org.example.bookingservicelogic.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bookingservicelogic.dto.request.UserCreateRequest;
import org.example.bookingservicelogic.dto.request.UserUpdateRequest;
import org.example.bookingservicelogic.dto.response.PageResponse;
import org.example.bookingservicelogic.dto.response.UserResponse;
import org.example.bookingservicelogic.entity.enums.Role;
import org.example.bookingservicelogic.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for user management operations.
 *
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Users", description = "User management API")
public class UserController {

    private final UserService userService;

    @PostMapping
    @Operation(summary = "Create a new user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        log.info("REST request to create user: {}", request.getUsername());
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("REST request to get user by ID: {}", id);
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/username/{username}")
    @Operation(summary = "Get user by username")
    public ResponseEntity<UserResponse> getUserByUsername(@PathVariable String username) {
        log.info("REST request to get user by username: {}", username);
        UserResponse response = userService.getUserByUsername(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("REST request to get user by email: {}", email);
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "Get all users with pagination")
    public ResponseEntity<PageResponse<UserResponse>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        log.info("REST request to get all users, page: {}, size: {}", page, size);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PageResponse<UserResponse> response = userService.getAllUsers(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/role/{role}")
    @Operation(summary = "Get users by role")
    public ResponseEntity<PageResponse<UserResponse>> getUsersByRole(
            @PathVariable Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to get users by role: {}", role);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserResponse> response = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    @Operation(summary = "Search users")
    public ResponseEntity<PageResponse<UserResponse>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("REST request to search users with query: {}", query);
        Pageable pageable = PageRequest.of(page, size);
        PageResponse<UserResponse> response = userService.searchUsers(query, pageable);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        log.info("REST request to update user: {}", id);
        UserResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/role")
    @Operation(summary = "Change user role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @RequestParam Role role) {
        log.info("REST request to change role for user {} to {}", id, role);
        UserResponse response = userService.changeUserRole(id, role);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/role")
    @Operation(summary = "Change user role (PUT alternative)")
    public ResponseEntity<UserResponse> changeUserRolePut(
            @PathVariable Long id,
            @RequestParam Role role) {
        log.info("REST request (PUT) to change role for user {} to {}", id, role);
        UserResponse response = userService.changeUserRole(id, role);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/enabled")
    @Operation(summary = "Enable or disable user")
    public ResponseEntity<UserResponse> setUserEnabled(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        log.info("REST request to set enabled={} for user {}", enabled, id);
        UserResponse response = userService.setUserEnabled(id, enabled);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/enabled")
    @Operation(summary = "Enable or disable user (PUT alternative)")
    public ResponseEntity<UserResponse> setUserEnabledPut(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        log.info("REST request (PUT) to set enabled={} for user {}", enabled, id);
        UserResponse response = userService.setUserEnabled(id, enabled);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("REST request to delete user: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
