package com.techtrek.booking.exception.booking;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.BookingException;
import org.springframework.http.HttpStatus;

public class InvalidDateRangeException
        extends BaseException
        implements BookingException {

    public InvalidDateRangeException(String message) {
        // 400 - CheckOut before CheckIn, or dates from the past
        super(message, HttpStatus.BAD_REQUEST);
    }
}
