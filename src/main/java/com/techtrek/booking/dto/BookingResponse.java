package com.techtrek.booking.dto;

import java.time.LocalDate;
import com.techtrek.booking.entity.RoomType;

public record BookingResponse (
        Long id,
        String roomNumber,
        RoomType roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        String status
)
{}

