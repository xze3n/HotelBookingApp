package com.techtrek.booking.service;

import com.techtrek.booking.dto.CreateRoomRequest;
import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.dto.UpdateRoomRequest;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.RoomType;
import com.techtrek.booking.exception.room.DuplicateRoomNumberException;
import com.techtrek.booking.exception.room.RoomNotFoundException;
import com.techtrek.booking.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomServiceImpl roomService;

    private Room room;

    @BeforeEach
    void setUp(){
        room = new Room();
        room.setId(1L);
        room.setRoomNumber("1");
        room.setType(RoomType.DOUBLE);
        room.setCapacity(2);
        room.setPricePerNight(new BigDecimal("150.00"));
        room.setDescription("");
        room.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void createRoom_ShouldReturnRoomResponse_WhenRoomIsValid(){
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber("1");
        request.setType(RoomType.DOUBLE);
        request.setCapacity(2);
        request.setPricePerNight(new BigDecimal("150.00"));
        request.setDescription("");

        when(roomRepository.existsByRoomNumber("1")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse response = roomService.createRoom(request);

        assertNotNull(response);
        assertEquals("1", response.getRoomNumber());
        assertEquals(RoomType.DOUBLE, response.getType());
        assertEquals(2, response.getCapacity());
        verify(roomRepository).existsByRoomNumber("1");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void createRoom_ShouldThrowException_WhenRoomNumberAlreadyExists(){
        CreateRoomRequest request = new CreateRoomRequest();
        request.setRoomNumber("1");
        request.setType(RoomType.DOUBLE);
        request.setCapacity(2);
        request.setPricePerNight(new BigDecimal("150.00"));

        when(roomRepository.existsByRoomNumber("1")).thenReturn(true);

        assertThrows(DuplicateRoomNumberException.class, ()->roomService.createRoom(request));

        verify(roomRepository).existsByRoomNumber("1");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void getAllRooms_ShouldReturnListOfRoomResponses(){
        when(roomRepository.findAll()).thenReturn(List.of(room));

        List<RoomResponse> result = roomService.getAllRooms();

        assertEquals(1, result.size());
        assertEquals("1", result.getFirst().getRoomNumber());
        verify(roomRepository).findAll();
    }


    @Test
    void getRoomById_ShouldReturnRoom_WhenRoomExists(){
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        RoomResponse response = roomService.getRoomById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("1", response.getRoomNumber());
        verify(roomRepository).findById(1L);
    }

    @Test
    void getRoomById_ShouldThrowException_WhenRoomDoesNotExist(){
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, ()->roomService.getRoomById(1L));

        verify(roomRepository).findById(1L);
    }

    @Test
    void updateRoom_ShouldUpdateRoom_WhenRoomExistsAndNumberIsUnique(){
        UpdateRoomRequest request = new UpdateRoomRequest();
        request.setRoomNumber("2");
        request.setType(RoomType.SUITE);
        request.setCapacity(3);
        request.setPricePerNight(new BigDecimal("250.00"));
        request.setDescription("Updated Room");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByRoomNumber("2")).thenReturn(false);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        RoomResponse response = roomService.updateRoom(1L, request);

        assertNotNull(response);
        verify(roomRepository).findById(1L);
        verify(roomRepository).existsByRoomNumber("2");
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void updateRoom_ShouldThrowException_WhenRoomDoesNotExist(){
        UpdateRoomRequest request = new UpdateRoomRequest();
        request.setRoomNumber("2");
        request.setType(RoomType.SUITE);
        request.setCapacity(3);
        request.setPricePerNight(new BigDecimal("250.00"));

        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, ()-> roomService.updateRoom(1L, request));

        verify(roomRepository).findById(1L);
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void updateRoom_ShouldThrowException_WhenNewRoomNumberAlreadyExists(){
        UpdateRoomRequest request = new UpdateRoomRequest();
        request.setRoomNumber("2");
        request.setType(RoomType.SUITE);
        request.setCapacity(3);
        request.setPricePerNight(new BigDecimal("250.00"));

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.existsByRoomNumber("2")).thenReturn(true);

        assertThrows(DuplicateRoomNumberException.class, ()-> roomService.updateRoom(1L, request));

        verify(roomRepository).findById(1L);
        verify(roomRepository).existsByRoomNumber("2");
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void deleteRoom_ShouldDeleteRoom_WhenRoomExists(){
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        roomService.deleteRoom(1L);

        verify(roomRepository).findById(1L);
        verify(roomRepository).delete(room);
    }

    @Test
    void deleteRoom_ShouldThrowException_WhenRoomDoesNotExist(){
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoomNotFoundException.class, () -> roomService.deleteRoom(1L));

        verify(roomRepository).findById(1L);
        verify(roomRepository, never()).delete(any(Room.class));
    }


    @Test
    void searchAvailableRooms_ShouldReturnMappedRoomResponse_WhenFiltersAreValid(){
        LocalDate startDate = LocalDate.of(2026, 4, 10);
        LocalDate endDate = LocalDate.of(2026, 4, 12);

        Room availableRoom = new Room();
        availableRoom.setId(2L);
        availableRoom.setRoomNumber("2");
        availableRoom.setType(RoomType.DOUBLE);
        availableRoom.setCapacity(2);
        availableRoom.setPricePerNight(new BigDecimal("200.00"));
        availableRoom.setDescription("");
        availableRoom.setCreatedAt(LocalDateTime.now());

        when(roomRepository.findAvailableRooms(startDate, endDate, RoomType.DOUBLE, 2))
                .thenReturn(List.of(availableRoom));

        List<RoomResponse> result = roomService.searchAvailableRooms(startDate, endDate, "double", "2");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("2", result.getFirst().getRoomNumber());
        assertEquals(RoomType.DOUBLE, result.getFirst().getType());
        assertEquals(2, result.getFirst().getCapacity());

        verify(roomRepository).findAvailableRooms(startDate, endDate, RoomType.DOUBLE, 2);
    }

    @Test
    void searchAvailableRooms_ShouldPassNullFilters_WhenTypeAndCapacityAreNull(){
        LocalDate startDate = LocalDate.of(2026, 4, 10);
        LocalDate endDate = LocalDate.of(2026, 4, 12);

        when(roomRepository.findAvailableRooms(startDate, endDate, null, null))
                .thenReturn(List.of(room));

        List<RoomResponse> result = roomService.searchAvailableRooms(
                startDate, endDate, null, null);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(room.getRoomNumber(), result.getFirst().getRoomNumber());

        verify(roomRepository).findAvailableRooms(startDate, endDate, null, null);
    }

    @Test
    void searchAvailableRooms_ShouldThrowException_WhenCapacityIsNotANumber(){
        LocalDate startDate = LocalDate.of(2026, 4, 10);
        LocalDate endDate = LocalDate.of(2026, 4, 12);
        assertThrows(NumberFormatException.class,
                () -> roomService.searchAvailableRooms(startDate, endDate,
                        "double", "asdf"));

        verify(roomRepository, never()).findAvailableRooms(any(), any(), any(), any());
    }

    @Test
    void searchAvailableRooms_ShouldPassNullRoomType_WhenTypeIsInvalid(){
        LocalDate startDate = LocalDate.of(2026, 4, 10);
        LocalDate endDate = LocalDate.of(2026, 4, 12);

        when(roomRepository.findAvailableRooms(startDate, endDate, null, 4))
                .thenReturn(List.of(room));

        List<RoomResponse> result = roomService.searchAvailableRooms(
                startDate, endDate, "4 rooms", "4");

        assertNotNull(result);
        assertEquals(1, result.size());

        verify(roomRepository).findAvailableRooms(startDate, endDate, null, 4);
    }
}











