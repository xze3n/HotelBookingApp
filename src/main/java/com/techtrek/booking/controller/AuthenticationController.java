package com.techtrek.booking.controller;

import com.techtrek.booking.dto.AuthRequestResponse.*;
import com.techtrek.booking.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication", description = "Endpoints for user registration and login")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService service;

    @Operation(summary = "Register a new user", description = "Creates a new customer account and returns a JWT token.")
    @ApiResponse(responseCode = "200", description = "User successfully registered.")
    @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., missing fields or bad email format).")
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(service.register(request));
    }

    @Operation(summary = "Login an existing user", description = "Authenticates a user by email and password, returning a JWT token.")
    @ApiResponse(responseCode = "200", description = "User successfully authenticated.")
    @ApiResponse(responseCode = "400", description = "Invalid input data format.")
    @ApiResponse(responseCode = "401", description = "Invalid credentials (wrong email or password).")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(service.login(request));
    }
}
