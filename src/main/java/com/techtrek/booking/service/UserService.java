package com.techtrek.booking.service;

import com.techtrek.booking.dto.*;
import com.techtrek.booking.entity.Role;
import com.techtrek.booking.entity.User;
import com.techtrek.booking.exception.EmailAlreadyExistsException;
import com.techtrek.booking.exception.UserNotFoundException;
import com.techtrek.booking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // Create user
    public UserResponse createUser(UserRegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(request.password());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(Role.CUSTOMER);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    // Get all users (admin)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Get user by id (admin)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return toResponse(user);
    }

    // Get user by email (used for /users/me)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        return toResponse(user);
    }

    // Update by email (used for /users/me)
    public UserResponse updateUser(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return toResponse(userRepository.save(user));
    }

    // Update by id (admin)
    public UserResponse updateUserById(Long id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        return toResponse(userRepository.save(user));
    }

    // Delete user (admin)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    // Map entity to DTO
    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}