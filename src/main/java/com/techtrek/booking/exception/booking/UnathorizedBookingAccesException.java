package com.techtrek.booking.exception.booking;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.BookingException;
import org.springframework.http.HttpStatus;

public class UnathorizedBookingAccesException
        extends BaseException
        implements BookingException {

    public UnathorizedBookingAccesException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
