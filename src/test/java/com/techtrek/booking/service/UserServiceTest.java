package com.techtrek.booking.service;

import com.techtrek.booking.dto.UserRegisterRequest;
import com.techtrek.booking.dto.UserResponse;
import com.techtrek.booking.dto.UserUpdateRequest;
import com.techtrek.booking.entity.Role;
import com.techtrek.booking.entity.User;
import com.techtrek.booking.exception.user.UserAlreadyExistsException;
import com.techtrek.booking.exception.user.UserNotFoundException;
import com.techtrek.booking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .password("password123")
                .firstName("John")
                .lastName("Doe")
                .role(Role.CUSTOMER)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createUser_ShouldReturnUserResponse_WhenEmailIsUnique() {
        UserRegisterRequest request = new UserRegisterRequest(
                "newuser@example.com", "pass", "Jane", "Doe"
        );
        
        when(userRepository.existsByEmail(request.email())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(2L);
            savedUser.setCreatedAt(LocalDateTime.now());
            return savedUser;
        });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(2L, response.id());
        assertEquals("newuser@example.com", response.email());
        assertEquals("Jane", response.firstName());
        assertEquals(Role.CUSTOMER, response.role());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        UserRegisterRequest request = new UserRegisterRequest(
                "test@example.com", "pass", "Jane", "Doe"
        );

        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_ShouldReturnListOfUserResponses() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<UserResponse> responses = userService.getAllUsers();

        assertEquals(1, responses.size());
        assertEquals(mockUser.getEmail(), responses.get(0).email());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void getUserById_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponse response = userService.getUserById(1L);

        assertEquals(mockUser.getEmail(), response.email());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void getUserByEmail_ShouldReturnUserResponse_WhenUserExists() {
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));

        UserResponse response = userService.getUserByEmail(mockUser.getEmail());

        assertEquals(mockUser.getId(), response.id());
        verify(userRepository, times(1)).findByEmail(mockUser.getEmail());
    }

    @Test
    void updateUser_ShouldReturnUpdatedUserResponse_WhenUserExists() {
        UserUpdateRequest request = new UserUpdateRequest("Johnny", "Smith");
        
        when(userRepository.findByEmail(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = userService.updateUser(mockUser.getEmail(), request);

        assertEquals("Johnny", response.firstName());
        assertEquals("Smith", response.lastName());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void updateUserById_ShouldReturnUpdatedUserResponse_WhenUserExists() {
        UserUpdateRequest request = new UserUpdateRequest("Johnny", "Smith");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse response = userService.updateUserById(1L, request);

        assertEquals("Johnny", response.firstName());
        assertEquals("Smith", response.lastName());
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void deleteUser_ShouldDelete_WhenUserExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.existsById(2L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(2L));
        verify(userRepository, never()).deleteById(anyLong());
    }
}
