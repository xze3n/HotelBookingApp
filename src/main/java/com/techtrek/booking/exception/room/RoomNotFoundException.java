package com.techtrek.booking.exception.room;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.RoomException;
import org.springframework.http.HttpStatus;

public class RoomNotFoundException
        extends BaseException
        implements RoomException {

    public RoomNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND); // 404
    }
}
