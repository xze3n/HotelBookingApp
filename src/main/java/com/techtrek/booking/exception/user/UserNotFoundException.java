package com.techtrek.booking.exception.user;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.UserException;
import org.springframework.http.HttpStatus;

public class UserNotFoundException
        extends BaseException
        implements UserException {

    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND); // 404
    }
}
