package com.techtrek.booking.service;

import com.techtrek.booking.dto.BookingResponse;
import com.techtrek.booking.dto.BookingUpdateRequest;
import com.techtrek.booking.dto.CreateBookingRequest;
import java.util.List;

public interface BookingService {

    //CREEARE BOOKING
    BookingResponse createBooking(CreateBookingRequest request);

    //RETURNARE TOATE REZERVARI
    List<BookingResponse> getAllBookings();

    //ANULEAZA O REZERVARE DUPA ID
    void cancelBooking(Long bookingId);

    //UPDATEAZA O REZERVARE DUPA ID
    BookingResponse updateBooking(Long bookingId, BookingUpdateRequest request);

    List<BookingResponse> getBookingsForCurrentUser();
}