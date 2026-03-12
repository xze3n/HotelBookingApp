package com.techtrek.booking.service;

import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.CreateBookingRequest;

public interface BookingService {
    BookingResponse createBooking(CreateBookingRequest request);
}