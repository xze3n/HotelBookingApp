package com.techtrek.booking.dto;

import com.techtrek.booking.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        Role role,
        LocalDateTime createdAt
) {}