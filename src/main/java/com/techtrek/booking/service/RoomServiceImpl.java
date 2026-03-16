package com.techtrek.booking.service;

import com.techtrek.booking.dto.CreateRoomRequest;
import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.exception.room.DuplicateRoomNumberException;
import com.techtrek.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;

    @Override
    public RoomResponse createRoom(CreateRoomRequest request){
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())){
            throw new DuplicateRoomNumberException("Room number " + request.getRoomNumber() + " already exists.");
        }

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setType(request.getType());
        room.setCapacity(request.getCapacity());
        room.setPricePerNight(request.getPricePerNight());
        room.setDescription(request.getDescription());

        Room savedRoom =  roomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    @Override
    public List<RoomResponse> getAllRooms(){
        return roomRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private RoomResponse mapToResponse(Room room){
        return new RoomResponse(
                room.getId(),
                room.getRoomNumber(),
                room.getType(),
                room.getCapacity(),
                room.getPricePerNight(),
                room.getDescription(),
                room.getCreatedAt()
        );
    }


}
