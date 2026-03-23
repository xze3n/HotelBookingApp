package com.techtrek.booking.controller;

import com.techtrek.booking.dto.*;
import com.techtrek.booking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Users", description = "User management endpoints")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /users/me - get own profile
    @Operation(summary = "Get current user profile", description = "Returns the profile of the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "Profile successfully retrieved.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserByEmail(userDetails.getUsername()));
    }

    // PUT /users/me - update own profile
    @Operation(summary = "Update current user profile", description = "Updates the profile of the currently authenticated user.")
    @ApiResponse(responseCode = "200", description = "Profile successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUser(userDetails.getUsername(), request));
    }

    // GET /users - list all (admin)
    @Operation(summary = "List all users", description = "Returns a list of all registered users. (Admin only)")
    @ApiResponse(responseCode = "200", description = "Users listed successfully.")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // GET /users/{id} - get one (admin)
    @Operation(summary = "Get a user by id", description = "Returns the details of a user with the specified id. (Admin only)")
    @ApiResponse(responseCode = "200", description = "User successfully retrieved.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // PUT /users/{id} - update one (admin)
    @Operation(summary = "Update a user", description = "Updates an existing user. (Admin only)")
    @ApiResponse(responseCode = "200", description = "User successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUserById(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(userService.updateUserById(id, request));
    }

    // DELETE /users/{id} - delete (admin)
    @Operation(summary = "Delete a user", description = "Deletes a user by id. (Admin only)")
    @ApiResponse(responseCode = "204", description = "User successfully deleted.")
    @ApiResponse(responseCode = "404", description = "User not found.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // POST /users - create user (admin)
    @Operation(summary = "Create a new user", description = "Creates a new user. (Admin only)")
    @ApiResponse(responseCode = "200", description = "User successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid request.")
    @ApiResponse(responseCode = "409", description = "Email already exists.")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody UserRegisterRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }
}