package com.techtrek.booking.exception.booking;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.BookingException;
import org.springframework.http.HttpStatus;

public class UnathorizedBookingAccesException
        extends BaseException
        implements BookingException {

    public UnathorizedBookingAccesException(String message) {
        // 400 - CheckOut before CheckIn, or dates from the past
        super(message, HttpStatus.FORBIDDEN);
    }
}
