package com.techtrek.booking.repository;

import com.techtrek.booking.dto.RoomResponse;
import com.techtrek.booking.entity.Room;
import com.techtrek.booking.entity.RoomType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT r FROM Room r WHERE " +
            "(:roomType IS NULL OR r.type = :roomType) AND " +
            "(:cap IS NULL OR r.capacity = :cap ) AND " +
            "NOT EXISTS (SELECT b FROM Booking b WHERE b.room = r AND " +
            "               b.checkInDate < :endDate AND b.checkOutDate > :startDate)")
    List<Room> findAvailableRooms(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("roomType") RoomType roomType,
            @Param("cap")Integer cap
    );

    boolean existsByRoomNumber(String roomNumber);

    List<Room> findByType(@NotNull(message = "Room type is required") RoomType roomType);

}
