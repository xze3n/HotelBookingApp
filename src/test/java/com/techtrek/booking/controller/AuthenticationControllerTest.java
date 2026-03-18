package com.techtrek.booking.controller;

import com.techtrek.booking.dto.AuthRequestResponse.RegisterRequest;
import com.techtrek.booking.dto.AuthRequestResponse.LoginRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // Spring's built-in tool for converting Java Objects to JSON strings

    @Test
    void shouldRegisterUserAndReturnTokenSuccessfully() throws Exception {
        RegisterRequest request = RegisterRequest.builder()
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .password("secure123")
                .build();

        // Simulate a Postman POST request
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // Expect a 200 OK status
                .andExpect(jsonPath("$.token").exists()); // Expect a JWT token in the JSON response
    }

    @Test
    void shouldBlockUnauthenticatedAccessToProtectedRoutes() throws Exception {
        // Because of the 'anyRequest().authenticated()' rule in SecurityConfiguration,
        // hitting ANY undefined or protected route without a Bearer token should fail.
        mockMvc.perform(get("/api/secure-dummy-route"))
                .andExpect(status().isForbidden()); // Expect a 403 Forbidden status
    }

    @Test
    void shouldLoginUserAndReturnTokenSuccessfully() throws Exception {
        // we register a unique user for this specific test
        RegisterRequest registerReq = RegisterRequest.builder()
                .firstName("Login")
                .lastName("Tester")
                .email("login_tester@example.com") //unique email to avoid conflicts
                .password("mypassword123")
                .build();

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk());

        // attempt to log in with those credentials
        LoginRequest loginReq = LoginRequest.builder()
                .email("login_tester@example.com")
                .password("mypassword123")
                .build();

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk()) //expect 200 OK
                .andExpect(jsonPath("$.token").exists()); //expect a new JWT token
    }
}