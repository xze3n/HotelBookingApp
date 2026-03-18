package com.techtrek.booking.service;

import com.techtrek.booking.config.JwtService;

import com.techtrek.booking.repository.UserRepository;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthenticationServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtService jwtService;
    @Mock private AuthenticationManager authenticationManager;

    void register_shouldEncodePasswordAndreturnToken() {
        
    }
}
