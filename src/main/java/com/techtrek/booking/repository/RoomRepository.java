package com.techtrek.booking.repository;

import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.RoomType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    boolean existsByRoomNumber(String roomNumber);

    List<Room> findByType(@NotNull(message = "Room type is required") RoomType roomType);
}
