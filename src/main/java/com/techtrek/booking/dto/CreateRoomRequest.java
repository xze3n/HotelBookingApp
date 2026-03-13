package com.techtrek.booking.dto;

import com.techtrek.booking.entity.RoomType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateRoomRequest {

    @NotBlank(message = "Room number is required.")
    private String roomNumber;

    @NotNull(message = "Room type is required.")
    private RoomType type;

    @Positive(message = "Capacity must be greater than zero.")
    private int capacity;

    @NotNull(message = "Price per night is required.")
    @DecimalMin(value = "0.0", inclusive = true, message = "Price per night must be greater than or equal to zero")
    private BigDecimal pricePerNight;

    private String description;

}
