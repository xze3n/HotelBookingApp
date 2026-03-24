package com.techtrek.booking.controller;

import com.techtrek.booking.dto.UserRegisterRequest;
import com.techtrek.booking.dto.UserResponse;
import com.techtrek.booking.dto.UserUpdateRequest;
import com.techtrek.booking.entity.Role;
import com.techtrek.booking.service.UserService;
import lombok.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private UserResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new org.springframework.web.method.support.HandlerMethodArgumentResolver() {
                    @Override
                    public boolean supportsParameter(@NonNull org.springframework.core.MethodParameter parameter) {
                        return parameter.getParameterType().isAssignableFrom(org.springframework.security.core.userdetails.UserDetails.class);
                    }
                    @Override
                    public Object resolveArgument(@NonNull org.springframework.core.MethodParameter parameter, org.springframework.web.method.support.ModelAndViewContainer mavContainer, @NonNull org.springframework.web.context.request.NativeWebRequest webRequest, org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                        return new org.springframework.security.core.userdetails.User(
                                "test@example.com", "pass", java.util.Collections.emptyList());
                    }
                })
                .build();
        
        mockResponse = new UserResponse(
                1L,
                "test@example.com",
                "John",
                "Doe",
                Role.CUSTOMER,
                LocalDateTime.now()
        );
    }

    @Test
    void getMe_ShouldReturnCurrentUser() throws Exception {
        when(userService.getUserByEmail("test@example.com")).thenReturn(mockResponse);

        // Mock principal
        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "test@example.com", "pass", Collections.emptyList()), null);

        mockMvc.perform(get("/users/me").principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void updateMe_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("Jane", "Smith");
        
        UserResponse updatedResponse = new UserResponse(
                1L, "test@example.com", "Jane", "Smith", Role.CUSTOMER, LocalDateTime.now()
        );

        when(userService.updateUser(eq("test@example.com"), any(UserUpdateRequest.class)))
                .thenReturn(updatedResponse);

        UsernamePasswordAuthenticationToken principal = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "test@example.com", "pass", Collections.emptyList()), null);

        mockMvc.perform(put("/users/me")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.lastName").value("Smith"));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(mockResponse));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].email").value("test@example.com"));
    }

    @Test
    void getUserById_ShouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(mockResponse);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void updateUserById_ShouldReturnUpdatedUser() throws Exception {
        UserUpdateRequest request = new UserUpdateRequest("Admin", "Account");
        
        UserResponse updatedResponse = new UserResponse(
                1L, "admin@example.com", "Admin", "Account", Role.ADMIN, LocalDateTime.now()
        );

        when(userService.updateUserById(eq(1L), any(UserUpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Admin"));
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void createUser_ShouldReturnCreatedUser() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest(
                "newuser@example.com", "pass123", "New", "User"
        );
        
        UserResponse createdResponse = new UserResponse(
                2L, "newuser@example.com", "New", "User", Role.CUSTOMER, LocalDateTime.now()
        );

        when(userService.createUser(any(UserRegisterRequest.class))).thenReturn(createdResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("newuser@example.com"));
    }
}
