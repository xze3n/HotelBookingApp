package com.techtrek.booking.exception;

public class DuplicateRoomNumberException extends RuntimeException{
    public DuplicateRoomNumberException(String roomNumber){
        super("Room number" + roomNumber + "already exists.");
    }
}
