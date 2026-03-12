package com.techtrek.booking.exception.booking;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.BookingException;
import org.springframework.http.HttpStatus;

public class BookingNotFoundException
        extends BaseException
        implements BookingException {

    public BookingNotFoundException(String message) {
        // 404 - Booking doesn't exists in DB
        super(message, HttpStatus.NOT_FOUND);
    }
}
