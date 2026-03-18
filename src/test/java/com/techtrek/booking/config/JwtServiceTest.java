package com.techtrek.booking.config;

import com.techtrek.booking.entity.Role;
import com.techtrek.booking.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private JwtService jwtService;
    private User dummyUser;

    @BeforeEach
    void setUp() {
        //manually initializing the service, without springboot
        jwtService = new JwtService();

        //injecting a false key, but eligible for the HS256 algorithm
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");

        //creating a dummy user for the test
        dummyUser = new User();
        dummyUser.setEmail("test@example.com");
        dummyUser.setPassword("password123");
        dummyUser.setRole(Role.CUSTOMER);
    }

    @Test
    void generateToken_shouldReturnValidString() {
        String token = jwtService.generateToken(dummyUser);

        assertNotNull(token);
        assertTrue(token.length() > 50);//jwt is always a long String
    }

    @Test
    void extractUsername_shouldReturnCorrectEmail() {
        String token = jwtService.generateToken(dummyUser);
        String extractedEmail = jwtService.extractUsername(token);

        assertEquals("test@example.com", extractedEmail);
    }

    @Test
    void isTokenValid_shouldReturnTrueForCorrectUser() {
        String token = jwtService.generateToken(dummyUser);

        assertTrue(jwtService.isTokenValid(token, dummyUser));
    }
}