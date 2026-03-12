package com.techtrek.booking.exception.booking;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.BookingException;
import org.springframework.http.HttpStatus;

public class BookingAlreadyCancelledException
        extends BaseException
        implements BookingException {

    public BookingAlreadyCancelledException(String message) {
        // 409 - booking already canceled
        super(message, HttpStatus.CONFLICT);
    }
}
