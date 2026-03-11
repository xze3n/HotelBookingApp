package com.techtrek.booking.exception.booking;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.BookingException;
import org.springframework.http.HttpStatus;

public class BookingConflictException
        extends BaseException
        implements BookingException {
    public BookingConflictException(String message) {
        //409 - Overlapping booking dates
        super(message, HttpStatus.CONFLICT);
    }
}
