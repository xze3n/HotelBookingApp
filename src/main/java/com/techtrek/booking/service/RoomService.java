package com.techtrek.booking.service;

import com.techtrek.booking.dto.CreateRoomRequest;
import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.dto.UpdateRoomRequest;

import java.util.List;

public interface RoomService {
    RoomResponse createRoom(CreateRoomRequest request);

    List<RoomResponse> getAllRooms();

    RoomResponse getRoomById(Long id);

    RoomResponse updateRoom(Long id, UpdateRoomRequest request);

    void deleteRoom(Long id);
}
