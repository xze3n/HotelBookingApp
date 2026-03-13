package com.techtrek.booking.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.techtrek.booking.entity.RoomType;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    @NotNull(message = "Room type is required")
    private RoomType roomType;

    @NotNull(message = "Check in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check out date is required")
    private LocalDate checkOutDate;


}
