package com.techtrek.booking.service;

import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.dto.CreateBookingRequest;
import java.util.List;

public interface BookingService {

    //CREATE BOOKING
    BookingResponse createBooking(CreateBookingRequest request);

    //RETURN ALL BOOKINGS
    List<BookingResponse> getAllBookings();

    //CANCEL A BOOKING BY ID
    void cancelBooking(Long bookingId);

    //UPDATE A BOOKING BY ID
    BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request);

    List<BookingResponse> getBookingsForCurrentUser();

    BookingResponse getBookingById(Long id);

    // Get upcoming bookings
    List<BookingResponse> getUpcomingBookings();
}