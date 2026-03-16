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
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    //POST /bookings CREATE BOOKING
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {

        BookingResponse response = bookingService.createBooking(request);
        return ResponseEntity.ok(response);

    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookingResponse> bookings = bookingService.getAllBookings();
        return ResponseEntity.ok(bookings);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id){
        bookingService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping{"/id"}
    public ResponseEntity<BookingResponse> updateBooking(
            @PathVariable Long id,
            @Valid @RequestBody BookingUpdateRequest request ){
        BookingResponse updateBooking = bookingService.updateBooking(id, request);
        return ResponseEntity.ok(updateBooking);

    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> getMyBookings(){
        List<BookingResponse> myBookings = bookingService.getBookingsForCurrentUser(){
            return ResponseEntity.ok(bookingService.getBookingsForCurrentUser());
        }
    }
}
