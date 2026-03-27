package com.techtrek.booking.service;

import com.techtrek.booking.security.JwtService;
import com.techtrek.booking.dto.AuthRequestResponse.RegisterRequest;
import com.techtrek.booking.dto.AuthRequestResponse.AuthResponse;
import com.techtrek.booking.entity.User;
import com.techtrek.booking.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;

    @SuppressWarnings("unused")
    @Mock private AuthenticationManager authenticationManager;

    // @InjectMocks takes the real class and "stuffs" the above mock clones into it
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    void register_shouldEncodePasswordAndReturnToken() {
        // setup
        RegisterRequest request = RegisterRequest.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john@example.com")
                .password("plainPassword")
                .build();

        when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");

        when(jwtService.generateToken(any(User.class))).thenReturn("fake-jwt-token");

        AuthResponse response = authenticationService.register(request);

        assertNotNull(response);
        assertEquals("fake-jwt-token", response.getToken());

        verify(userRepository, times(1)).save(any(User.class));
    }
}