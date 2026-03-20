package com.techtrek.booking.service;

import com.techtrek.booking.dto.CreateRoomRequest;
import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.dto.UpdateRoomRequest;
import com.techtrek.booking.entity.Booking;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.RoomType;
import com.techtrek.booking.exception.room.DuplicateRoomNumberException;
import com.techtrek.booking.exception.room.RoomNotFoundException;
import com.techtrek.booking.repository.BookingRepository;
import com.techtrek.booking.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    private final RoomRepository roomRepository;

    @Override
    public RoomResponse createRoom(CreateRoomRequest request){
        if (roomRepository.existsByRoomNumber(request.getRoomNumber())){
            throw new DuplicateRoomNumberException("Room with number "
                    + request.getRoomNumber() + "already exists");
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

    @Override
    public RoomResponse getRoomById(Long id){
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room with id " + id + "not found"));

        return mapToResponse(room);
    }


    @Override
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request){
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room with id " + id + " not found"));

        if(!existingRoom.getRoomNumber().equals(request.getRoomNumber())
                && roomRepository.existsByRoomNumber(request.getRoomNumber())) {
            throw new DuplicateRoomNumberException("Room with number "
                    + request.getRoomNumber() + "already exists");
        }

        existingRoom.setRoomNumber(request.getRoomNumber());
        existingRoom.setType(request.getType());
        existingRoom.setCapacity(request.getCapacity());
        existingRoom.setPricePerNight(request.getPricePerNight());
        existingRoom.setDescription(request.getDescription());

        Room updatedRoom =  roomRepository.save(existingRoom);
        return mapToResponse(updatedRoom);
    }

    @Override
    public void deleteRoom(Long id) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new RoomNotFoundException("Room with id " + id + " not found"));

        roomRepository.delete(existingRoom);
    }

    @Override
    public List<RoomResponse> searchAvailableRooms(LocalDate startDate, LocalDate endDate, String type, String capacity) {

        final RoomType roomType = (type == null) ? null
           : switch (type.toLowerCase()){
                case "single" -> RoomType.SINGLE;
                case "double" -> RoomType.DOUBLE;
                case "suite"  -> RoomType.SUITE;
                default -> null;
           };

        Integer cap = (capacity != null) ? Integer.parseInt(capacity) : null;


        return roomRepository.findAvailableRooms(startDate,endDate,roomType, cap)
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
