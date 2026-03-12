package com.techtrek.booking.exception.user;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.UserException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException
        extends BaseException
        implements UserException {

    public UserAlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT); // 409
    }
}
