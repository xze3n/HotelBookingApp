package com.techtrek.booking.controller;

import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.dto.CreateBookingRequest;
import com.techtrek.booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@Tag(name = "Booking", description = "Booking endpoints.")
@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Create a new booking", description = "Creates a new booking for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Booking successfully created.")
    @ApiResponse(responseCode = "400", description = "Invalid Date Range.")
    @ApiResponse(responseCode = "409", description = "No available rooms for the selected dates.")
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Get all bookings", description = "Returns all bookings. Admin only.")
    @ApiResponse(responseCode = "200", description = "Bookings successfully retrieved.")
    @ApiResponse(responseCode = "403", description = "Access Denied.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<BookingResponse>> getAllBookings() {
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
    @Operation(summary = "Cancel a booking.", description = "Cancels a booking by id. Owner only.")
    @ApiResponse(responseCode = "204", description = "Booking successfully cancelled.")
    @ApiResponse(responseCode = "403", description = "Not authorized to cancel this booking.")
    @ApiResponse(responseCode = "404", description = "Booking not found.")
    @ApiResponse(responseCode = "409", description = "Booking is already cancelled.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update a booking.", description = "Updates an existing booking. Owner only.")
    @ApiResponse(responseCode = "200", description = "Booking successfully updated.")
    @ApiResponse(responseCode = "400", description = "Invalid date range.")
    @ApiResponse(responseCode = "403", description = "Not authorized to update this booking.")
    @ApiResponse(responseCode = "404", description = "Booking not found.")
    @ApiResponse(responseCode = "409", description = "No available rooms for the selected dates.")
    @PutMapping("/{id}")
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateRequest request) {
        BookingResponse updateBooking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updateBooking);

    }

    @Operation(summary = "Get booking by ID.", description = "Return the details of a booking. Admin only.")
    @ApiResponse(responseCode = "200", description = "Booking successfully retrieved.")
    @ApiResponse(responseCode = "403", description = "Access denied.")
    @ApiResponse(responseCode = "404", description = "Booking not found.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable Long id) {
        BookingResponse booking = bookingService.getBookingById(id);
        return ResponseEntity.ok(booking);
    }
    @Operation(summary = "Get my bookings.", description = "Returns all bookings for the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Bookings successfully retrieved.")
    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings() {
        List<BookingResponse> myBookings = bookingService.getBookingsForCurrentUser();
        return ResponseEntity.ok(myBookings);
    }
    @Operation(summary = "Get upcoming bookings.", description = "Returns all upcoming bookings. Admin only.")
    @ApiResponse(responseCode = "200", description = "Bookings successfully retrieved.")
    @ApiResponse(responseCode = "403", description = "Access denied.")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/upcoming")
    public ResponseEntity<List<BookingResponse>> getUpcomingBookings() {
        List<BookingResponse> upcomingBookings = bookingService.getUpcomingBookings();
        return ResponseEntity.ok(upcomingBookings);
    }

}
