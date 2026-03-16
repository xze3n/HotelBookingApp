package com.techtrek.booking.exception.room;

import com.techtrek.booking.exception.base.BaseException;
import com.techtrek.booking.exception.base.RoomException;
import org.springframework.http.HttpStatus;

public class DuplicateRoomNumberException
        extends BaseException
        implements RoomException {
    public DuplicateRoomNumberException(String message){
        // 409 - Room already exists
        super(message, HttpStatus.CONFLICT);
    }
}
