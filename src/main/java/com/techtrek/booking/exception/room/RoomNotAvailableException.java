package com.techtrek.booking.exception.room;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.RoomException;
import org.springframework.http.HttpStatus;

public class RoomNotAvailableException
        extends BaseException
        implements RoomException {

    public RoomNotAvailableException(String message) {
        // 409 - room is used
        super(message, HttpStatus.CONFLICT);
    }
}
