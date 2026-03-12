package com.techtrek.booking.exception.room;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.RoomException;
import org.springframework.http.HttpStatus;

public class InvalidRoomFilterException
        extends BaseException
        implements RoomException {

    public InvalidRoomFilterException(String message) {
        // 400 - invalid filter
        super(message, HttpStatus.BAD_REQUEST);
    }
}
